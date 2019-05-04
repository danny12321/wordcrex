package nl.avans.wordcrex.model;

import java.util.List;

public class Round {
    public final int id;
    public final int round;
    public final List<Character> deck;

    public Round(int id, int round, List<Character> deck) {
        this.id = id;
        this.round = round;
        this.deck = deck;
    }
}
