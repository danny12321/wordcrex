package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int round;
    public final List<Character> characters;
    public final Turn hostTurn;
    public final Turn opponentTurn;
    public final List<Played> board;

    public Round(int round, List<Character> characters, Turn hostTurn, Turn opponentTurn, List<Played> board) {
        this.round = round;
        this.characters = characters;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
        this.board = board;
    }
}
