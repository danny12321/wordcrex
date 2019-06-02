package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.HistoryView;

import java.util.List;
import java.util.function.Function;

public class HistoryController extends Controller<Game> {
    public HistoryController(Main main, Function<User, Game> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new HistoryView(this);
    }

    public List<Round> getRounds() {
        return this.getModel().rounds;
    }

    public String getHost() {
        return this.getModel().host;
    }

    public int getHostScore() {
        return this.getModel().getHostScore();
    }

    public String getOpponent() {
        return this.getModel().opponent;
    }

    public int getOpponentScore() {
        return this.getModel().getOpponentScore();
    }

    public void navigateGame() {
        this.main.openController(GameController.class, StreamUtil.getModelProperty((user) -> user.games, (game) -> game.id == this.getModel().id));
    }
}
