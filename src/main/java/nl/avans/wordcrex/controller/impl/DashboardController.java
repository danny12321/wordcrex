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

    public boolean isCurrentUser(String user) {
        return user.equals(this.getModel().username);
    }

    public boolean isSelectable(Game game) {
        return game.state != GameState.PENDING || (game.inviteState == InviteState.PENDING && this.isCurrentUser(game.opponent));
    }

    public String getLabel(Game game) {
        if (game.state == GameState.PENDING) {
            return "UITDAGINGEN";
        } else if (game.state == GameState.FINISHED) {
            return "AFGELOPEN";
        } else {
            return "SPELLEN";
        }
    }

    public String getBigExtra(Game game) {
        return game.state == GameState.PENDING ? this.isCurrentUser(game.host) ? "Naar " : "Van " : "";
    }

    public String getSmallExtra(Game game) {
        if (game.state == GameState.PENDING && game.inviteState == InviteState.ACCEPTED || game.getLastRound() == null) {
            return "Wachten op bevestiging - ";
        } else if (game.state == GameState.PLAYING) {
            return this.isCurrentUser(game.host) == (game.getLastRound().hostTurn == null) ? "Jouw beurt - " : "Hun beurt - ";
        } else if (game.state == GameState.FINISHED || game.state == GameState.RESIGNED) {
            return game.winner.equals(this.getModel().username) ? "Je hebt gewonnen - " : "Je hebt verloren - ";
        }

        return "";
    }

    public List<Game> getGames() {
        return this.getModel().games;
    }

    public void acceptInvite(Game game) {
        this.getModel().respondToInvite(game, InviteState.ACCEPTED);
    }

    public void rejectInvite(Game game) {
        this.getModel().respondToInvite(game, InviteState.REJECTED);
    }

    public void navigateGame(int id) {
        this.main.openController(IngameController.class, StreamUtil.getModelProperty((user) -> user.games, (game) -> game.id == id));
    }

    public void navigateInvite() {
        this.main.openController(InviteController.class);
    }
}
