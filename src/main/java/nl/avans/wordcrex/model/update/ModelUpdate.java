package nl.avans.wordcrex.model.update;

import nl.avans.wordcrex.model.Match;

import java.util.List;

public class ModelUpdate {
    public final List<Match> matches;

    public ModelUpdate(List<Match> matches) {
        this.matches = matches;
    }
}
