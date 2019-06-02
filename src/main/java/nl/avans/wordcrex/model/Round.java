package nl.avans.wordcrex.model;

public class Round {
    public final int round;
    public final Turn hostTurn;
    public final Turn opponentTurn;

    public Round(int round, Turn hostTurn, Turn opponentTurn) {
        this.round = round;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
    }
}
