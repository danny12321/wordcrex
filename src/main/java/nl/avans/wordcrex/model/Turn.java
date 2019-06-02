package nl.avans.wordcrex.model;

import java.util.List;

public class Turn {
    public final TurnAction action;
    public final int score;
    public final int bonus;
    public final List<Played> played;

    public Turn(TurnAction action, int score, int bonus, List<Played> played) {
        this.action = action;
        this.score = score;
        this.bonus = bonus;
        this.played = played;
    }
}
