package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.Wordcrex;

import java.util.function.Function;

public class ObservingController extends AbstractGameController {
    private int round;

    public ObservingController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public boolean canPlay() {
        return false;
    }

    @Override
    public Round getRound() {
        return this.getModel().rounds.get(this.round);
    }

    @Override
    public void nextRound() {
        if (this.round < this.getTotalRounds() - 1) {
            this.round++;
        }
    }

    @Override
    public void previousRound() {
        if (this.round > 0) {
            this.round--;
        }
    }
}
