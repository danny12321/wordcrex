package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.GameState;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ObserveView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ObserveController extends Controller<User> {
    private String search = "";
    private List<Game> games = new ArrayList<>();

    public ObserveController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ObserveView(this);
    }

    @Override
    public void poll() {
        super.poll();

        this.games = this.getModel().findObservableGames(this.search);
    }

    public String getLabel(Game game) {
        if (game.state == GameState.PLAYING) {
            return "BEZIG";
        } else {
            return "AFGELOPEN";
        }
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void navigateGame(Game game) {
        this.main.openController(ObserveGameController.class, (model) -> game);
    }
}
