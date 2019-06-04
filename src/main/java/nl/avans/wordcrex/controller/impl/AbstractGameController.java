package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.GameView;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractGameController extends Controller<Game> {
    public AbstractGameController(Main main, Function<User, Game> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new GameView(this);
    }

    public abstract String getScore();

    public abstract String getHostName();

    public abstract String getOpponentName();

    public abstract List<Tile> getTiles();

    public abstract List<Played> getBoard();

    public abstract List<Character> getDeck();

    public abstract int getPoolSize();

    public abstract void startNewRound();

    public abstract int getNewScore(List<Played> played);

    public abstract Character getPlaceholder();

    public abstract void navigateChat();

    public abstract void navigateHistory();
}
