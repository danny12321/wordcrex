package nl.avans.wordcrex.model.update;

import nl.avans.wordcrex.model.Round;

import java.util.List;

public class MatchUpdate {
    public final List<Round> rounds;

    public MatchUpdate(List<Round> rounds) {
        this.rounds = rounds;
    }
}
