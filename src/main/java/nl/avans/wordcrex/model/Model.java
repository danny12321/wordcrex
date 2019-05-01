package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;

public class Model {
    private Board board;
    private Player player;

    public void poll(Database database) {
    }

    public boolean login(Database database, String username, String password) {
        var ref = new Object() {
            String firstName;
            String lastName;
        };
        var successful = database.execute(
            "SELECT u.first_name, u.last_name FROM \"user\" u WHERE u.username = ? AND u.password = ?",
            (statement) -> {
                statement.setString(1, username);
                statement.setString(2, password);
            },
            (result) -> {
                ref.firstName = result.getString("first_name");
                ref.lastName = result.getString("last_name");
            }
        );

        if (!successful) {
            return false;
        }

        this.player = new Player(username, ref.firstName, ref.lastName);

        database.execute(
            "SELECT r.role FROM role r JOIN user_roles ur ON r.id = ur.role_id JOIN \"user\" u ON ur.user_id = u.id AND u.username = ?",
            (statement) -> statement.setString(1, username),
            (result) -> this.player.addRole(Role.byName(result.getString("role")))
        );

        return true;
    }

    public Player getPlayer() {
        return this.player;
    }
}
