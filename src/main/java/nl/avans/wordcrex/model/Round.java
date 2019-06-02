package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int round;
    public final List<Character> characters;
    public final Turn hostTurn;
    public final Turn opponentTurn;

    public Round(int round, List<Character> characters, Turn hostTurn, Turn opponentTurn) {
        this.round = round;
        this.characters = characters;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
    }
}
