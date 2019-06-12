package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.ObserveView;

import java.util.List;
import java.util.function.Function;

public class ObserveController extends Controller<User> {
    public ObserveController(Main main, Function<Wordcrex, User> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update((model) -> model.poll(UserPoll.OBSERVABLE));
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new ObserveView(this);
    }

    public String getLabel(Game game) {
        if (game.state == GameState.PENDING) {
            return "UITDAGINGEN";
        } else if (game.state == GameState.PLAYING) {
            return "SPELLEN";
        } else {
            return "AFGELOPEN";
        }
    }

    public List<Game> getGames() {
        return this.getModel().observable;
    }

    public boolean canClick(Game game) {
        return game.getLastRound() != null;
    }

    public void clickGame(Game game) {
        this.main.openController(ObservingController.class, StreamUtil.getModelProperty((model) -> model.user.observable, (g) -> g.id == game.id));
    }
}
