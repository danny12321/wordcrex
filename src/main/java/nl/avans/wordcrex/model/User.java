package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.Persistable;

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
        if (poll == UserPoll.GAMES) {
            return new User(this.database, this.wordcrex, this.username, this.roles, this.words, Game.initialize(this.database, this.wordcrex, this.username), this.observable, this.manageable);
        }

        return this;
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
        throw new RuntimeException();
    }

    public void respondInvite(Game game, GameState state) {
        throw new RuntimeException();
    }

    public void suggestWord(String word, Dictionary dictionary) {
        throw new RuntimeException();
    }

    public void respondSuggestion(Word word, WordState state) {
        throw new RuntimeException();
    }

    public void changePassword(String password) {
        throw new RuntimeException();
    }

    public void setRoles(String username, List<UserRole> roles) {
        throw new RuntimeException();
    }
}
