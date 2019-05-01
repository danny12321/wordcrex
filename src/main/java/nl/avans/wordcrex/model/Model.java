package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final List<Match> matches = new ArrayList<>();

    private Player player;

    public void poll(Database database) {
        if (this.player == null) {
            return;
        }

        var matches = new ArrayList<Match>();
        database.execute(
            "SELECT m.id, m.status, h.id host_id, h.username host_username, h.first_name host_first_name, h.last_name host_last_name, o.id opponent_id, o.username opponent_username, o.first_name opponent_first_name, o.last_name opponent_last_name FROM match m JOIN \"user\" h ON m.host_id = h.id JOIN \"user\" o ON m.opponent_id = o.id WHERE m.host_id = ? OR m.opponent_id = ? ORDER BY m.status",
            (statement) -> {
                statement.setInt(1, this.player.id);
                statement.setInt(2, this.player.id);
            },
            (result) -> {
                var id = result.getInt("id");
                var status = result.getInt("status");
                var current = this.matches.stream().filter((m) -> m.id == id && m.status.status == status).findFirst().orElse(null);

                if (current != null) {
                    matches.add(current);

                    return;
                }

                var hostId = result.getInt("host_id");
                var opponentId = result.getInt("opponent_id");
                var host = hostId == this.player.id ? this.player : new Player(hostId, result.getString("host_username"), result.getString("host_first_name"), result.getString("host_last_name"));
                var opponent = opponentId == this.player.id ? this.player : new Player(opponentId, result.getString("opponent_username"), result.getString("opponent_first_name"), result.getString("opponent_last_name"));

                matches.add(new Match(id, host, opponent, Match.Status.byStatus(status)));
            }
        );
        this.matches.clear();
        this.matches.addAll(matches);
    }

    public boolean login(Database database, String username, String password) {
        var ref = new Object() {
            int id;
            String firstName;
            String lastName;
        };
        var successful = database.execute(
            "SELECT u.id, u.first_name, u.last_name FROM \"user\" u WHERE u.username = ? AND u.password = ?",
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

        if (!successful) {
            return false;
        }

        this.player = new Player(ref.id, username, ref.firstName, ref.lastName);

        database.execute(
            "SELECT r.role FROM role r JOIN user_roles ur ON r.id = ur.role_id JOIN \"user\" u ON ur.user_id = u.id AND u.username = ?",
            (statement) -> statement.setString(1, username),
            (result) -> this.player.addRole(Role.byName(result.getString("role")))
        );

        return true;
    }

    public void logout() {
        this.player = null;
        this.matches.clear();
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Match> getMatches() {
        return List.copyOf(this.matches);
    }
}
