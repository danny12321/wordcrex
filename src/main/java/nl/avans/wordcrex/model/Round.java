package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int round;
    public final List<Character> character;
    public final Turn hostTurn;
    public final Turn opponentTurn;

    public Round(int round, List<Character> character, Turn hostTurn, Turn opponentTurn) {
        this.round = round;
        this.character = character;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
    }
}
