package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.Pollable;

import java.util.*;

public class User implements Pollable<User> {
    private final Database database;

    public final String username;
    public final List<UserRole> roles;
    public final List<Game> games;
    public final List<Dictionary> dictionaries;

    private User currentUserBeingEdited = this;

    public User(Database database) {
        this(database, "");
    }

    public User(Database database, String username) {
        this(database, username, List.of(), List.of(), List.of());
    }

    public User(User user, List<UserRole> roles, List<Dictionary> dictionaries) {
        this(user.database, user.username, roles, user.games, dictionaries);
    }

    public User(User user, List<Game> games) {
        this(user.database, user.username, user.roles, games, user.dictionaries);
    }

    public User(Database database, String username, List<UserRole> roles, List<Game> games, List<Dictionary> dictionaries) {
        this.database = database;
        this.username = username;
        this.roles = roles;
        this.games = games;
        this.dictionaries = dictionaries;
    }

    @Override
    public User initialize() {
        if (!this.dictionaries.isEmpty()) {
            return this;
        }

        var roles = new ArrayList<UserRole>();

        this.database.select(
            "SELECT r.role FROM accountrole r WHERE r.username = ?",
            (statement) -> statement.setString(1, this.username),
            (result) -> roles.add(UserRole.byRole(result.getString("role")))
        );

        var characters = new HashMap<String, List<Character>>();

        this.database.select(
            "SELECT c.letterset_code code, c.symbol `character`, c.value, c.counted amount FROM symbol c",
            (result) -> {
                var code = result.getString("code");
                var list = characters.getOrDefault(code, new ArrayList<>());

                list.add(new Character(result.getString("character"), result.getInt("value"), result.getInt("amount")));

                characters.put(code, list);
            }
        );

        var dictionaries = new ArrayList<Dictionary>();

        this.database.select(
            "SELECT d.* FROM letterset d",
            (result) -> {
                var code = result.getString("code");
                var character = characters.getOrDefault(code, new ArrayList<>());

                if (character.isEmpty()) {
                    return;
                }

                dictionaries.add(new Dictionary(this.database, code, result.getString("description"), List.copyOf(character)));
            }
        );

        return new User(this, List.copyOf(roles), List.copyOf(dictionaries));
    }

    @Override
    public User poll() {
        if (this.username.isEmpty()) {
            return this;
        }

        var games = new ArrayList<Game>();

        this.database.select(
            "SELECT g.game_id id, g.game_state state, g.answer_player2 invite_state, g.username_player1 host, g.username_player2 opponent, g.letterset_code code " +
                "FROM game g " +
                "WHERE (g.username_player1 = ? OR g.username_player2 = ?) " +
                "AND g.answer_player2 != ?",
            (statement) -> {
                statement.setString(1, this.username);
                statement.setString(2, this.username);
                statement.setString(3, InviteState.REJECTED.state);
            },
            (result) -> {
                var id = result.getInt("id");
                var state = GameState.byState(result.getString("state"));
                var inviteState = InviteState.byState(result.getString("invite_state"));
                var host = result.getString("host");
                var opponent = result.getString("opponent");
                var code = result.getString("code");
                var dictionary = this.dictionaries.stream()
                    .filter((d) -> d.code.equals(code))
                    .findAny()
                    .orElse(null);

                if (dictionary == null) {
                    System.out.println("Dictionary not found: " + code);

                    return;
                }

                var game = new Game(this.database, id, host, opponent, state, inviteState, dictionary);

                if (game.state == GameState.PENDING && game.inviteState == InviteState.ACCEPTED && this.username.equals(game.host)) {
                    game.startGame();
                }

                games.add(game);
            }
        );

        games.sort(Comparator.comparingInt((game) -> game.state.order));

        return new User(this, List.copyOf(games));
    }

    @Override
    public User persist(User user) {
        return this;
    }

    public User register(String username, String password) {
        var insertedUser = this.database.insert(
            "INSERT INTO account VALUES (?, ?)",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, password);
            }
        );

        var insertedRole = this.database.insert(
            "INSERT INTO accountrole VALUES (?, ?)",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, UserRole.PLAYER.role);
            }
        );

        if (insertedUser == -1 || insertedRole == -1) {
            return this;
        }

        return this.login(username, password);
    }

    public User login(String username, String password) {
        var ref = new Object() {
            String username;
        };
        var selected = this.database.select(
            "SELECT a.username FROM account a WHERE lower(a.username) = lower(?) AND lower(a.password) = lower(?)",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, password);
            },
            (result) -> ref.username = result.getString("username")
        );

        if (selected == 0) {
            return this;
        }

        return new User(this.database, ref.username);
    }

    public User logout() {
        return new User(this.database);
    }

    public boolean hasRole(UserRole role) {
        return this.roles.indexOf(role) != -1;
    }

    public List<Pair<String, Boolean>> findOpponents(String username) {
        if (!this.hasRole(UserRole.PLAYER) || username.isEmpty()) {
            return List.of();
        }

        var users = new ArrayList<Pair<String, Boolean>>();

        this.database.select(
            "SELECT a.username," +
                "       (SELECT count(*) = 0" +
                "        FROM game g" +
                "        WHERE ((g.username_player1 = ? AND g.username_player2 = a.username)" +
                "            OR (g.username_player1 = a.username AND g.username_player2 = ?))" +
                "          AND g.game_state IN (?, ?)) enabled " +
                "FROM account a" +
                "         JOIN accountrole r ON a.username = r.username AND r.role = ?" +
                "WHERE a.username != ?" +
                "  AND a.username LIKE ?",
            (statement) -> {
                statement.setString(1, this.username);
                statement.setString(2, this.username);
                statement.setString(3, GameState.PENDING.state);
                statement.setString(4, GameState.PLAYING.state);
                statement.setString(5, UserRole.PLAYER.role);
                statement.setString(6, this.username);
                statement.setString(7, "%" + username + "%");
            },
            (result) -> users.add(new Pair<>(result.getString("username"), result.getBoolean("enabled")))
        );

        return List.copyOf(users);
    }

    public void sendInvite(String username, Dictionary dictionary) {
        if (!this.hasRole(UserRole.PLAYER)) {
            return;
        }

        this.database.insert(
            "INSERT INTO game (game_state, letterset_code, username_player1, username_player2, answer_player2) VALUES (?, ?, ?, ?, ?)",
            (statement) -> {
                statement.setString(1, GameState.PENDING.state);
                statement.setString(2, dictionary.code);
                statement.setString(3, this.username);
                statement.setString(4, username);
                statement.setString(5, InviteState.PENDING.state);
            }
        );
    }

    public void respondToInvite(Game game, InviteState state) {
        if (!this.hasRole(UserRole.PLAYER) || !game.opponent.equals(this.username)) {
            return;
        }

        this.database.update(
            "UPDATE game g SET g.answer_player2 = ? WHERE g.game_id = ?",
            (statement) -> {
                statement.setString(1, state.state);
                statement.setInt(2, game.id);
            }
        );
    }

    public List<Word> getPendingWords() {
        if (!this.hasRole(UserRole.MODERATOR)) {
            return List.of();
        }

        var words = new ArrayList<Word>();

        this.database.select(
            "SELECT w.word, w.state, w.username FROM dictionary w WHERE w.state = ? ",
            (statement) -> statement.setString(1, WordState.PENDING.state),
            (result) -> words.add(new Word(result.getString("word"), WordState.byState(result.getString("state")), result.getString("username")))
        );

        return List.copyOf(words);
    }

    public boolean suggestWord(String word, Dictionary dictionary) {
        if (dictionary.isWord(word)) {
            return false;
        }

        this.database.insert(
            "INSERT INTO dictionary VALUES (?, ?, ?, ?)",
            (statement) -> {
                statement.setString(1, word);
                statement.setString(2, dictionary.code);
                statement.setString(3, WordState.PENDING.state);
                statement.setString(4, this.username);
            }
        );

        return true;
    }

    public Map<String, List<Word>> getSuggested(int page) {
        var size = 100;
        var words = new HashMap<String, List<Word>>();

        this.database.select(
            "SELECT w.word, w.letterset_code code, w.state FROM dictionary w WHERE w.username = ? LIMIT ?, ?",
            (statement) -> {
                statement.setString(1, this.username);
                statement.setInt(2, page * size);
                statement.setInt(3, size);
            },
            (result) -> {
                var code = result.getString("code");
                var list = words.getOrDefault(code, new ArrayList<>());

                list.add(new Word(result.getString("word"), WordState.byState(result.getString("state")), this.username));

                words.put(code, list);
            }
        );

        return Map.copyOf(words);
    }

    public void changePassword(String password) {
        this.database.update(
            "UPDATE account SET password = ? WHERE username = ?",
            (statement) -> {
                statement.setString(1, password);
                statement.setString(2, this.username);
            }
        );
    }

    public void switchRole(UserRole role) {
        if (this.roles.contains(role)) {
            this.database.delete(
                "DELETE FROM accountrole WHERE role = ? AND username = ?",
                (statement) -> {
                    statement.setString(1, role.toString().toLowerCase());
                    statement.setString(2, this.username);
                }
            );
        } else {
            this.database.insert(
                "INSERT INTO accountrole (role, username) VALUES (?, ?)",
                (statement) -> {
                    statement.setString(1, role.toString().toLowerCase());
                    statement.setString(2, this.username);
                }
            );
        }
    }

    public List<User> getChangeableUsers(String name) {
        var users = new ArrayList<User>();

        var sql = "SELECT username, role FROM wordcrex.accountrole WHERE username LIKE ? AND username != ? ";

        if (name.isEmpty()) {
            return users;
        } else if (name.equals("ALL")) { //get all users including logged in user
            sql = "SELECT username, role FROM wordcrex.accountrole";

            this.database.select(sql,
                (statement) -> {
                    if (!name.equals("ALL")) {
                        statement.setString(1, name + "%");
                        statement.setString(2, this.username);
                    }
                },
                (result) -> {
                    var roleList = new ArrayList<UserRole>();
                    var foundUser = false;
                    for (User u : users) {
                        if (u.username.equals(result.getString("username"))) {
                            u.roles.add(UserRole.byRole(result.getString("role")));
                            foundUser = true;
                            break;
                        }
                    }
                    if (!foundUser) {
                        roleList.add(UserRole.byRole(result.getString("role")));
                        users.add(new User(this.database, result.getString("username"), roleList, null, null));
                    }
                }
            );
            return users;
        }

        return users;
    }

    public User getCurrentUserBeingEdited() {
        if (this.roles.contains(UserRole.ADMINISTRATOR)) {
            return this.currentUserBeingEdited;
        } else {
            return this;
        }
    }

    public void setCurrentUserBeingEdited(User user) {
        currentUserBeingEdited = user;
    }
}
