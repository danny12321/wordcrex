package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.ArrayList;
import java.util.List;

public class User implements Pollable<User> {
    private final Database database;

    public final String username;
    public final boolean authenticated;
    public final List<UserRole> roles;
    public final List<Game> games;

    public User(Database database) {
        this(database, "", false);
    }

    public User(Database database, String username, boolean authenticated) {
        this(database, username, authenticated, List.of(), List.of());
    }

    public User(User user, List<UserRole> roles, List<Game> games) {
        this(user.database, user.username, user.authenticated, roles, games);
    }

    public User(Database database, String username, boolean authenticated, List<UserRole> roles, List<Game> games) {
        this.database = database;
        this.username = username;
        this.authenticated = authenticated;
        this.roles = roles;
        this.games = games;
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

                games.add(new Game(this.database, id, host, opponent, GameState.byState(state), InviteState.byState(inviteState)));
            }
        );

        return new User(this, List.copyOf(roles), List.copyOf(games));
    }

    @Override
    public User persist(User user) {
        return this;
    }

    public String getDisplayName() {
        return this.username;
    }

    public String getInitial() {
        var displayName = this.getDisplayName();

        if (displayName.isEmpty()) {
            return "?";
        }

        return displayName.substring(0, 1).toUpperCase();
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

    public List<String> getUsers(String username) {
        List<String> users = new ArrayList<>();

        if(username.isEmpty()) return users;

        this.database.select(
                "SELECT a.username FROM account a JOIN accountrole ar ON a.username = ar.username WHERE a.username LIKE ? AND a.username != ? AND ar.role = 'player'",
                (statement) -> {
                    statement.setString(1, username + "%");
                    statement.setString(2, this.username);
                },
                (result) -> users.add(result.getString("username"))
        );

        return users;
    }

    public User logout() {
        return new User(this.database);
    }
}
