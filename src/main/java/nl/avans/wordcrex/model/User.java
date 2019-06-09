package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.ListUtil;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.Persistable;
import nl.avans.wordcrex.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class User implements Persistable {
    private final Database database;
    private final Wordcrex wordcrex;

    public final String username;
    public final List<UserRole> roles;
    public final List<Word> words;
    public final List<Game> games;
    public final List<Game> observable;
    public final List<User> manageable;

    public User(Database database, Wordcrex wordcrex, String username, List<UserRole> roles, List<Word> words, List<Game> games, List<Game> observable, List<User> manageable) {
        this.database = database;
        this.wordcrex = wordcrex;
        this.username = username;
        this.roles = roles;
        this.words = words;
        this.games = games;
        this.observable = observable;
        this.manageable = manageable;
    }

    public static User initialize(Database database, Wordcrex wordcrex, String username, String password) {
        var ref = new Object() {
            String username;
            List<UserRole> roles = new ArrayList<>();
        };

        var selected = database.select(
            "SELECT a.username, group_concat(r.role) roles FROM account a JOIN accountrole r ON a.username = r.username WHERE lower(a.username) = lower(?) AND lower(a.password) = lower(?) GROUP BY a.username",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, password);
            },
            (result) -> {
                var rolesRaw = result.getString("roles").split(",");

                ref.username = result.getString("username");

                for (var role : rolesRaw) {
                    ref.roles.add(UserRole.byRole(role));
                }
            }
        );

        if (selected <= 0) {
            return null;
        }

        return new User(database, wordcrex, username, List.copyOf(ref.roles), List.of(), List.of(), List.of(), List.of());
    }

    @Override
    public Wordcrex persist(Wordcrex model) {
        return new Wordcrex(this.database, this, model.tiles, model.dictionaries);
    }

    public User poll(UserPoll poll) {
        var roles = new ArrayList<UserRole>();

        this.database.select(
            "SELECT r.role FROM accountrole r WHERE r.username = ?",
            (statement) -> statement.setString(1, this.username),
            (result) -> roles.add(UserRole.byRole(result.getString("role")))
        );

        if (poll == UserPoll.GAMES) {
            return new User(this.database, this.wordcrex, this.username, List.copyOf(roles), this.words, Game.initialize(this.database, this.wordcrex, this.username), this.observable, this.manageable);
        } else if (poll == UserPoll.WORDS) {
            var ref = new Object() {
                List<Word> words = new ArrayList<>();
            };

            this.database.select(
                "SELECT w.word, w.letterset_code dictionary_id, w.state FROM dictionary w WHERE w.username = ? ORDER BY w.letterset_code LIMIT 100",
                (statement) -> statement.setString(1, this.username),
                (result) -> {
                    var word = result.getString("word");
                    var state = WordState.byState(result.getString("state"));

                    var dictionaryId = result.getString("dictionary_id");
                    var dictionary = ListUtil.find(wordcrex.dictionaries, (d) -> d.id.equals(dictionaryId));

                    ref.words.add(new Word(word, this.username, state, dictionary));
                }
            );

            return new User(this.database, this.wordcrex, this.username, List.copyOf(roles), List.copyOf(ref.words), this.games, this.observable, this.manageable);
        }

        return new User(this.database, this.wordcrex, this.username, List.copyOf(roles), this.words, this.games, this.observable, this.manageable);
    }

    public boolean hasRole(UserRole role) {
        return this.roles.indexOf(role) != -1;
    }

    public List<Pair<String, Boolean>> findOpponents(String username) {
        if (!this.hasRole(UserRole.PLAYER)) {
            return List.of();
        }

        var ref = new Object() {
            List<Pair<String, Boolean>> opponents = new ArrayList<>();
        };

        this.database.select(
            "SELECT u.username, (SELECT count(*) = 0 FROM game g WHERE ((g.username_player1 = u.username AND g.username_player2 = ?) OR (g.username_player1 = ? AND g.username_player2 = u.username)) AND g.game_state IN (?, ?) AND g.answer_player2 != ?) available FROM account u JOIN accountrole r ON u.username = r.username WHERE u.username != ? AND u.username LIKE ? AND r.role = ?",
            (statement) -> {
                statement.setString(1, this.username);
                statement.setString(2, this.username);
                statement.setString(3, GameState.PENDING.state);
                statement.setString(4, GameState.PLAYING.state);
                statement.setString(5, InviteState.REJECTED.state);
                statement.setString(6, this.username);
                statement.setString(7, "%" + username + "%");
                statement.setString(8, UserRole.PLAYER.role);
            },
            (result) -> {
                var opponent = result.getString("username");
                var available = result.getBoolean("available");

                ref.opponents.add(new Pair<>(opponent, available));
            }
        );

        return List.copyOf(ref.opponents);
    }

    public void sendInvite(String username, Dictionary dictionary) {
        if (!this.hasRole(UserRole.PLAYER)) {
            return;
        }

        this.database.insert(
            "INSERT INTO game (game_state, letterset_code, username_player1, username_player2, answer_player2) VALUES (?, ?, ?, ?, ?)",
            (statement) -> {
                statement.setString(1, GameState.PENDING.state);
                statement.setString(2, dictionary.id);
                statement.setString(3, this.username);
                statement.setString(4, username);
                statement.setString(5, InviteState.PENDING.state);
            }
        );
    }

    public void respondInvite(Game game, GameState state) {
        throw new RuntimeException();
    }

    public boolean suggestWord(String word, Dictionary dictionary) {
        if (!StringUtil.isWordInput(word)) {
            return false;
        }

        var updated = this.database.insert(
            "INSERT INTO dictionary VALUES (?, ?, ?, ?)",
            (statement) -> {
                statement.setString(1, word);
                statement.setString(2, dictionary.id);
                statement.setString(3, WordState.PENDING.state);
                statement.setString(4, this.username);
            }
        );

        return updated != -1;
    }

    public void respondSuggestion(Word word, WordState state) {
        throw new RuntimeException();
    }

    public void changePassword(String password) {
        if (!StringUtil.isAuthInput(password)) {
            return;
        }

        this.database.update(
            "UPDATE account SET password = ? WHERE username = ?",
            (statement) -> {
                statement.setString(1, password);
                statement.setString(2, this.username);
            }
        );
    }

    public void toggleRole(User user, UserRole role) {
        if (!this.hasRole(UserRole.ADMINISTRATOR)) {
            return;
        }

        if (user.hasRole(role)) {
            if (user.roles.size() <= 1) {
                return;
            }

            this.database.update(
                "DELETE FROM accountrole WHERE role = ? AND username = ?",
                (statement) -> {
                    statement.setString(1, role.role);
                    statement.setString(2, user.username);
                }
            );
        } else {
            this.database.insert(
                "INSERT INTO accountrole VALUES (?, ?)",
                (statement) -> {
                    statement.setString(1, user.username);
                    statement.setString(2, role.role);
                }
            );
        }
    }
}
