package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.ArrayList;
import java.util.List;

public class User implements Pollable<User> {
    private final Database database;

    public final String username;
    public final List<Role> roles;
    public final List<Match> matches;

    public User(Database database) {
        this(database, "");
    }

    public User(Database database, String username) {
        this(database, username, List.of(), List.of());
    }

    public User(User user, List<Role> roles, List<Match> matches) {
        this(user.database, user.username, roles, matches);
    }

    public User(Database database, String username, List<Role> roles, List<Match> matches) {
        this.database = database;
        this.username = username;
        this.roles = roles;
        this.matches = matches;
    }

    @Override
    public User poll() {
        if (!this.isAuthenticated()) {
            return this;
        }

        var roles = new ArrayList<Role>();

        this.database.select(
            "SELECT role FROM accountrole WHERE username = ?",
            (statement) -> statement.setString(1, username),
            (result) -> roles.add(Role.byRole(result.getString("role")))
        );

        var matches = new ArrayList<Match>();

        this.database.select(
            "SELECT m.id, m.status, h.id host_id, h.username host_username, h.first_name host_first_name, h.last_name host_last_name, o.id opponent_id, o.username opponent_username, o.first_name opponent_first_name, o.last_name opponent_last_name FROM `match` m JOIN `user` h ON m.host_id = h.id JOIN `user` o ON m.opponent_id = o.id WHERE m.host_id = ? OR m.opponent_id = ? ORDER BY m.status",
            (statement) -> {
                statement.setString(1, this.username);
                statement.setString(2, this.username);
            },
            (result) -> {
                var id = result.getInt("id");
                var status = result.getInt("status");
                var host = result.getString("host_username") == this.username ? this : new User(this.database, result.getString("host_username"));
                var opponent = result.getString("opponent_username") == this.username ? this : new User(this.database, result.getString("opponent_username"));

                matches.add(new Match(this.database, 1, host, opponent, Match.Status.byStatus(status)));
            }
        );

        return new User(this, List.copyOf(roles), List.copyOf(matches));
    }

    @Override
    public User persist(User user) {
        return this;
    }

    public boolean isAuthenticated() {
        return !this.username.isEmpty();
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
            "SELECT username FROM `account` WHERE username = ? AND password = ?",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, password);
            },
            (result) -> {
                ref.username = result.getString("username");
            }
        );

        if (selected == 0) {
            return this;
        }

        return new User(this.database, ref.username);
    }

    public User logout() {
        return new User(this.database);
    }

    public enum Role {
        PLAYER("player"),
        OBSERVER("observer"),
        MODERATOR("moderator"),
        ADMINISTRATOR("administrator");

        public final String role;

        Role(String role) {
            this.role = role;
        }

        public static Role byRole(String role) {
            for (var r : Role.values()) {
                if (r.role.equals(role)) {
                    return r;
                }
            }

            return null;
        }
    }
}
