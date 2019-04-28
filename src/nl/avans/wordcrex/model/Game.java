package nl.avans.wordcrex.model;

public class Game {
    private Player host;
    private Player opponent;
    private Status status;

    public Game(Player host, Player opponent, Status status) {
        this.host = host;
        this.opponent = opponent;
        this.status = status;
    }

    public Player getHost() {
        return this.host;
    }

    public Player getOpponent() {
        return this.opponent;
    }

    public Status getStatus() {
        return this.status;
    }

    public enum Status {
        PENDING("INVITES", 0),
        PLAYING("YOUR TURN", 1),
        WAITING("THEIR TURN", 2),
        OVER("GAME OVER", 3),
        REJECTED("", 4);

        public final String name;
        public final int order;

        Status(String name, int order) {
            this.name = name;
            this.order = order;
        }
    }
}
