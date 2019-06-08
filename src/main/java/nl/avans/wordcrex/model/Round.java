package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int id;
    public final List<Played> board;
    public final List<Playable> deck;
    public final Turn hostTurn;
    public final Turn opponentTurn;

    public Round(int id, List<Played> board, List<Playable> deck, Turn hostTurn, Turn opponentTurn) {
        this.id = id;
        this.board = board;
        this.deck = deck;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
    }
}
