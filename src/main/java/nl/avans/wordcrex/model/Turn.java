package nl.avans.wordcrex.model;

import java.util.List;

public class Turn {
    public final int id;
    public final int score;
    public final int bonus;
    public final TurnAction action;
    public final List<Played> played;

    public Turn(int id, int score, int bonus, TurnAction action, List<Played> played) {
        this.id = id;
        this.score = score;
        this.bonus = bonus;
        this.action = action;
        this.played = played;
    }
}
