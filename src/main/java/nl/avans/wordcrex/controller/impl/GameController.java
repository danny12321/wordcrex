package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Played;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.Wordcrex;

import java.util.List;
import java.util.function.Function;

public class GameController extends AbstractGameController {
    public GameController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public List<Played> getPlayed() {
        return List.of();
    }

    @Override
    public Round getRound() {
        return this.getModel().getLastRound();
    }

    @Override
    public void nextRound() {
        throw new RuntimeException();
    }

    @Override
    public void previousRound() {
        throw new RuntimeException();
    }

    @Override
    public int getScore() {
        return 0;
    }
}
