package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.User;
import nl.avans.wordcrex.util.StreamUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.DashboardView;

import java.util.List;
import java.util.function.Function;

public class DashboardController extends Controller<User> {
    public DashboardController(Main main, Function<User, User> fn) {
        super(main, fn);
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new DashboardView(this);
    }

    public boolean isCurrentUser(User user) {
        return user.username == this.getModel().username;
    }

    public boolean canSelectMatch(Game game) {
        return game.status == Game.Status.PLAYING || (game.status == Game.Status.PENDING && !this.isCurrentUser(game.host));
    }

    public List<Game> getMatches() {
        return this.getModel().games;
    }

    public void navigateMatch(int id) {
        this.main.openController(MatchController.class, StreamUtil.getModelProperty((user) -> user.games, (match) -> match.id == id));
    }
}
