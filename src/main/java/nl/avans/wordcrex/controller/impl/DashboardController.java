package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.GameState;
import nl.avans.wordcrex.model.InviteState;
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
        return user.username.equals(this.getModel().username);
    }

    public boolean isVisible(Game game) {
        return (game.state == GameState.PENDING || game.state == GameState.PLAYING) && game.inviteState != InviteState.REJECTED;
    }

    public boolean isSelectable(Game game) {
        return game.state == GameState.PLAYING || (game.state == GameState.PENDING && !this.isCurrentUser(game.host));
    }

    public String getLabel(Game game) {
        if (game.state == GameState.PENDING) {
            return "UITGEDAAGD";
        } else if (game.turn) {
            return "JOUW BEURT";
        } else {
            return "HUN BEURT";
        }
    }

    public List<Game> getGames() {
        return this.getModel().games;
    }

    public void navigateGame(int id) {
        this.main.openController(GameController.class, StreamUtil.getModelProperty((user) -> user.games, (game) -> game.id == id));
    }

    public void newGame() {
        System.out.println("Open new game view");
        this.main.openController(NewGameController.class);
    }
}
