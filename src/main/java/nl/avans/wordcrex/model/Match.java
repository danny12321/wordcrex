package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;

public class Match {
    private final Database database;

    public final int id;
    public final Player host;
    public final Player opponent;
    public final Status status;

    public Match(Database database, int id, Player host, Player opponent, Status status) {
        this.database = database;
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.status = status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            return;
        }

        this.database.update(
            "UPDATE `match` SET status = ? WHERE id = ?",
            (statement) -> {
                statement.setInt(1, status.status);
                statement.setInt(2, this.id);
            }
        );
    }

    public enum Status {
        PENDING("INVITES", 0),
        PLAYING("PLAYING", 1),
        OVER("GAME OVER", 3),
        REJECTED("", 4);

        public final String name;
        public final int status;

        Status(String name, int status) {
            this.name = name;
            this.status = status;
        }

        public static Status byStatus(int s) {
            for (var status : Status.values()) {
                if (status.status == s) {
                    return status;
                }
            }

            return null;
        }
    }
}
