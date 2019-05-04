package nl.avans.wordcrex.model;

import nl.avans.wordcrex.Observable;
import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.model.update.ModelUpdate;

import java.util.ArrayList;
import java.util.List;

public class Model extends Observable<ModelUpdate> {
    private final Database database;

    private Player player;

    public Model(Database database) {
        super(new ModelUpdate(List.of()));
        this.database = database;
    }

    public void poll() {
        if (this.player == null) {
            return;
        }

        var last = this.getLast();
        var matches = new ArrayList<Match>();

        this.database.select(
            "SELECT m.id, m.status, h.id host_id, h.username host_username, h.first_name host_first_name, h.last_name host_last_name, o.id opponent_id, o.username opponent_username, o.first_name opponent_first_name, o.last_name opponent_last_name FROM `match` m JOIN `user` h ON m.host_id = h.id JOIN `user` o ON m.opponent_id = o.id WHERE m.host_id = ? OR m.opponent_id = ? ORDER BY m.status",
            (statement) -> {
                statement.setInt(1, this.player.id);
                statement.setInt(2, this.player.id);
            },
            (result) -> {
                var id = result.getInt("id");
                var status = result.getInt("status");
                var current = last.matches.stream()
                    .filter((m) -> m.id == id && m.status.status == status)
                    .findFirst()
                    .orElse(null);

                if (current != null) {
                    matches.add(current);

                    return;
                }

                var hostId = result.getInt("host_id");
                var opponentId = result.getInt("opponent_id");
                var host = hostId == this.player.id ? this.player : new Player(hostId, result.getString("host_username"), result.getString("host_first_name"), result.getString("host_last_name"), List.of());
                var opponent = opponentId == this.player.id ? this.player : new Player(opponentId, result.getString("opponent_username"), result.getString("opponent_first_name"), result.getString("opponent_last_name"), List.of());

                matches.add(new Match(this.database, id, host, opponent, Match.Status.byStatus(status)));
            }
        );

        matches.forEach(Match::poll);

        this.next(new ModelUpdate(List.copyOf(matches)));
    }

    public boolean login(String username, String password) {
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
            return false;
        }

        var roles = new ArrayList<Role>();

        this.database.select(
            "SELECT r.role FROM role r JOIN user_roles ur ON r.id = ur.role_id JOIN `user` u ON ur.user_id = u.id AND u.username = ?",
            (statement) -> statement.setString(1, username),
            (result) -> roles.add(Role.byRole(result.getString("role")))
        );

        this.player = new Player(ref.id, username, ref.firstName, ref.lastName, List.copyOf(roles));

        return true;
    }

    public void logout() {
        this.player = null;
        this.next(new ModelUpdate(List.of()));
    }

    public Player getPlayer() {
        return this.player;
    }
}
