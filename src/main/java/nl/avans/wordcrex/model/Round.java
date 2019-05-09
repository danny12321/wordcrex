package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;

import java.util.List;

public class Round {
    private final Database database;

    public final int id;
    public final int round;
    public final Match match;
    public final List<Character> deck;

    public Round(Database database, int id, int round, Match match, List<Character> deck) {
        this.database = database;
        this.id = id;
        this.round = round;
        this.match = match;
        this.deck = deck;
    }
}
