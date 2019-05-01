package nl.avans.wordcrex.model;

public class Match {
    public final int id;
    public final Player host;
    public final Player opponent;
    public final Status status;

    public Match(int id, Player host, Player opponent, Status status) {
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.status = status;
    }

    public enum Status {
        PENDING("INVITES", 0),
        PLAYING("YOUR TURN", 1),
        WAITING("THEIR TURN", 2),
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
