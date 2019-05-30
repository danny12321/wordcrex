package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int round;
    public final List<Played> played;
    public final Turn hostTurn;
    public final Turn opponentTurn;

    public Round(int round, List<Played> played, Turn hostTurn, Turn opponentTurn) {
        this.round = round;
        this.played = played;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
    }

    public boolean isHostTurn() {
        return this.hostTurn == null;
    }
}
