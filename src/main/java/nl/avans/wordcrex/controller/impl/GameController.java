package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.Wordcrex;

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
    public Round getRound() {
        throw new RuntimeException();
    }

    @Override
    public void nextRound() {
        throw new RuntimeException();
    }

    @Override
    public void previousRound() {
        throw new RuntimeException();
    }
}
