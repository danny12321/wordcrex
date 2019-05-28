package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class User implements Pollable<User> {
    private final Database database;

    public final String username;
    public final boolean authenticated;
    public final List<UserRole> roles;
    public final List<Game> games;
    public final List<Dictionary> dictionaries;

    public User(Database database) {
        this(database, "", false);
    }

    public User(Database database, String username, boolean authenticated) {
        this(database, username, authenticated, List.of(), List.of(), List.of());
    }

    public User(User user, List<Dictionary> dictionaries) {
        this(user.database, user.username, user.authenticated, user.roles, user.games, dictionaries);
    }

    public User(User user, List<UserRole> roles, List<Game> games) {
        this(user.database, user.username, user.authenticated, roles, games, user.dictionaries);
    }

    public User(Database database, String username, boolean authenticated, List<UserRole> roles, List<Game> games, List<Dictionary> dictionaries) {
        this.database = database;
        this.username = username;
        this.authenticated = authenticated;
        this.roles = roles;
        this.games = games;
        this.dictionaries = dictionaries;
    }

    @Override
    public User initialize() {
        if (!this.dictionaries.isEmpty()) {
            return this;
        }

        var characters = new HashMap<String, List<Character>>();

        this.database.select(
            "SELECT * FROM symbol",
            (statement) -> {},
            (result) -> {
                var code = result.getString("letterset_code");
                var list = characters.getOrDefault(code, new ArrayList<>());

                list.add(new Character(result.getString("symbol"), result.getInt("value"), result.getInt("counted")));

                characters.put(code, list);
            }
        );

        var words = new HashMap<String, List<Word>>();

        this.database.select(
            "SELECT * FROM dictionary",
            (statement) -> {},
            (result) -> {
                var code = result.getString("letterset_code");
                var list = words.getOrDefault(code, new ArrayList<>());

                list.add(new Word(result.getString("word"), WordState.byState(result.getString("state")), result.getString("username")));

                words.put(code, list);
            }
        );

        var dictionaries = new ArrayList<Dictionary>();

        this.database.select(
            "SELECT * FROM letterset",
            (statement) -> {},
            (result) -> {
                var code = result.getString("code");
                var character = characters.getOrDefault(code, new ArrayList<>());
                var word = words.getOrDefault(code, new ArrayList<>());

                dictionaries.add(new Dictionary(code, result.getString("description"), List.copyOf(character), List.copyOf(word)));
            }
        );

        return new User(this, List.copyOf(dictionaries));
    }

    @Override
    public User poll() {
        if (!this.authenticated) {
            return this;
        }

        var roles = new ArrayList<UserRole>();

        this.database.select(
            "SELECT r.role FROM accountrole r WHERE r.username = ?",
            (statement) -> statement.setString(1, username),
            (result) -> roles.add(UserRole.byRole(result.getString("role")))
        );

        var games = new ArrayList<Game>();

        this.database.select(
            "SELECT * FROM game g WHERE g.username_player1 = ? OR g.username_player2 = ?",
            (statement) -> {
                statement.setString(1, this.username);
                statement.setString(2, this.username);
            },
            (result) -> {
                var id = result.getInt("game_id");
                var state = result.getString("game_state");
                var inviteState = result.getString("answer_player2");
                var host = result.getString("username_player1").equals(this.username) ? this : new User(this.database, result.getString("username_player1"), false);
                var opponent = result.getString("username_player2").equals(this.username) ? this : new User(this.database, result.getString("username_player2"), false);
                var code = result.getString("letterset_code");
                var dictionary = this.dictionaries.stream()
                    .filter((d) -> d.code.equals(code))
                    .findAny()
                    .orElse(null);

                if (dictionary == null) {
                    return;
                }

                games.add(new Game(this.database, id, false, host, opponent, GameState.byState(state), InviteState.byState(inviteState), dictionary));
            }
        );

        return new User(this, List.copyOf(roles), List.copyOf(games));
    }

    @Override
    public User persist(User user) {
        return this;
    }

    public String getInitial() {
        if (this.username.isEmpty()) {
            return "?";
        }

        return this.username.substring(0, 1).toUpperCase();
    }

    public User login(String username, String password) {
        var ref = new Object() {
            String username;
        };
        var selected = this.database.select(
            "SELECT a.username FROM account a WHERE a.username = ? AND a.password = ?",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, password);
            },
            (result) -> ref.username = result.getString("username")
        );

        if (selected == 0) {
            return this;
        }

        return new User(this.database, ref.username, true);
    }

    public List<HashMap<String, String>> getUsers(String username) {
        List<HashMap<String, String>> users = new ArrayList<>();

        if(username.isEmpty()) return users;

        this.database.select(
                "SELECT  a.username, " +
                        "(SELECT true FROM game g WHERE ((g.username_player1 = ? AND g.username_player2 LIKE ?) OR (g.username_player2 = ? AND g.username_player1 LIKE ?)) AND g.game_state IN ('request', 'playing') AND g.answer_player2 IN ('unknown', 'accepted') LIMIT 1) " +
                        "as disabled FROM account a JOIN accountrole ar ON a.username = ar.username WHERE a.username LIKE ? AND a.username != ? AND ar.role = 'player';",
                (statement) -> {
                    statement.setString(1, this.username);
                    statement.setString(2, username + "%");
                    statement.setString(3, username + "%");
                    statement.setString(4, this.username);
                    statement.setString(5, username + "%");
                    statement.setString(6, this.username);


                },
                (result) -> {
                    HashMap<String, String> user = new HashMap<>();
                    user.put("username", result.getString("username"));
                    user.put("disabled", result.getString("disabled"));
                    users.add(user);
                }
        );

        return users;
    }

    public void sendInvite(String username, String letterset_code) {
        int game_id = this.database.insert(
                "INSERT INTO game(game_state, letterset_code, username_player1, username_player2, answer_player2)" +
                        "VALUES('request', ?, ?, ?, 'unknown')",
                (statement) -> {
                    statement.setString(1, letterset_code);
                    statement.setString(2, this.username);
                    statement.setString(3, username);
                }
        );

        this.database.insert(
                "INSERT INTO turn(game_id, turn_id)" +
                        "VALUES(?, 1)",
                (statement) -> statement.setInt(1, game_id)
        );

        List<String> letters = new ArrayList<>();

        this.database.select(
            "SELECT * from symbol WHERE letterset_code = ?",
            (statement) -> statement.setString(1, letterset_code),
            (result) -> {
                for(int i = 0; i < result.getInt("counted"); i++) {
                    letters.add(result.getString("symbol"));
                }
            }
        );

        for(int i = 0; i < letters.size(); i++) {
            String symbol = letters.get(i);
            int letter_id = i;

            this.database.insert(
                "INSERT INTO letter(letter_id, game_id, symbol_letterset_code, symbol)" +
                        "VALUES(?, ?, ?, ?)",
                (statement) -> {
                    statement.setInt(1, letter_id);
                    statement.setInt(2, game_id);
                    statement.setString(3, letterset_code);
                    statement.setString(4, symbol);
                }
            );
        }
    }

    public User logout() {
        return new User(this.database);
    }

    public Map<String, List<Word>> getSubmittedWords() {
        return Map.copyOf(this.dictionaries.stream()
            .collect(Collectors.groupingBy((dictionary) -> dictionary.code, Collectors.flatMapping((dictionary) -> dictionary.words.stream().filter((word) -> word.username.equals(this.username)), Collectors.toList()))));
    }
}
