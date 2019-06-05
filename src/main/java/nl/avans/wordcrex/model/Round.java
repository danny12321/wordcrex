package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int round;
    public final List<Letter> deck;
    public final Turn hostTurn;
    public final Turn opponentTurn;
    public final int hostScore;
    public final int opponentScore;
    public final List<Played> board;

    public Round(int round, List<Letter> deck, Turn hostTurn, Turn opponentTurn, int hostScore, int opponentScore, List<Played> board) {
        this.round = round;
        this.deck = deck;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
        this.hostScore = hostScore;
        this.opponentScore = opponentScore;
        this.board = board;
    }
}
