package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.ArrayList;
import java.util.List;

public class User implements Pollable<User> {
    private final Database database;

    public final int id;
    public final String username;
    public final String firstName;
    public final String lastName;
    public final List<Role> roles;
    public final List<Match> matches;

    public User(Database database) {
        this(database, 0, "", null, null);
    }

    public User(Database database, int id, String username, String firstName, String lastName) {
        this(database, id, username, firstName, lastName, List.of(), List.of());
    }

    public User(User user, List<Role> roles, List<Match> matches) {
        this(user.database, user.id, user.username, user.firstName, user.lastName, roles, matches);
    }

    public User(Database database, int id, String username, String firstName, String lastName, List<Role> roles, List<Match> matches) {
        this.database = database;
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
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
            "SELECT r.role FROM role r JOIN user_role ur ON r.id = ur.role_id JOIN `user` u ON ur.user_id = u.id AND u.username = ?",
            (statement) -> statement.setString(1, username),
            (result) -> roles.add(Role.byRole(result.getString("role")))
        );

        var matches = new ArrayList<Match>();

        this.database.select(
            "SELECT m.id, m.status, h.id host_id, h.username host_username, h.first_name host_first_name, h.last_name host_last_name, o.id opponent_id, o.username opponent_username, o.first_name opponent_first_name, o.last_name opponent_last_name FROM `match` m JOIN `user` h ON m.host_id = h.id JOIN `user` o ON m.opponent_id = o.id WHERE m.host_id = ? OR m.opponent_id = ? ORDER BY m.status",
            (statement) -> {
                statement.setInt(1, this.id);
                statement.setInt(2, this.id);
            },
            (result) -> {
                var id = result.getInt("id");
                var status = result.getInt("status");
                var host = result.getInt("host_id") == this.id ? this : new User(this.database, 0, result.getString("host_username"), result.getString("host_first_name"), result.getString("host_last_name"));
                var opponent = result.getInt("opponent_id") == this.id ? this : new User(this.database, 0, result.getString("opponent_username"), result.getString("opponent_first_name"), result.getString("opponent_last_name"));

                matches.add(new Match(this.database, id, host, opponent, Match.Status.byStatus(status)));
            }
        );

        return new User(this, List.copyOf(roles), List.copyOf(matches));
    }

    @Override
    public User persist(User user) {
        return this;
    }

    public boolean isAuthenticated() {
        return this.id > 0 || !this.username.isEmpty();
    }

    public String getDisplayName() {
        if (this.firstName != null && this.lastName != null) {
            return this.firstName + " " + this.lastName;
        }

        return this.username;
    }

    public String getInitial() {
        return this.getDisplayName().substring(0, 1).toUpperCase();
    }

    public User login(String username, String password) {
        var ref = new Object() {
            int id;
            String firstName;
            String lastName;
        };
        var selected = this.database.select(
            "SELECT u.id, u.first_name, u.last_name FROM `user` u WHERE u.username = ? AND u.password = ?",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, password);
            },
            (result) -> {
                ref.id = result.getInt("id");
                ref.firstName = result.getString("first_name");
                ref.lastName = result.getString("last_name");
            }
        );

        if (selected == 0) {
            return this;
        }

        return new User(this.database, ref.id, username, ref.firstName, ref.lastName);
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
