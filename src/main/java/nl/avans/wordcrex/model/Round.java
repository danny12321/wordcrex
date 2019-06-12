package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int id;
    public final List<Played> board;
    public final List<Playable> deck;
    public final int hostScore;
    public final int opponentScore;
    public final Turn hostTurn;
    public final Turn opponentTurn;

    public Round(int id, List<Played> board, List<Playable> deck, int hostScore, int opponentScore, Turn hostTurn, Turn opponentTurn) {
        this.id = id;
        this.board = board;
        this.deck = deck;
        this.hostScore = hostScore;
        this.opponentScore = opponentScore;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
    }
}
