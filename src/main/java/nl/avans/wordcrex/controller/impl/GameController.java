package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Tile;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.GameView;

import java.util.List;
import java.util.function.Function;

public class GameController extends Controller<Game> {
    public GameController(Main main, Function<User, Game> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new GameView(this);
    }

    public String getScore() {
        return this.getModel().hostScore + " - " + this.getModel().opponentScore;
    }

    public String getHostName() {
        return this.getModel().host.username;
    }

    public String getOpponentName() {
        return this.getModel().opponent.username;
    }

    public List<Tile> getTiles() {
        return this.getModel().tiles;
    }
}
