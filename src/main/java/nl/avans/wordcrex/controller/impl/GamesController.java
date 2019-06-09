package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.GamesView;

import java.util.List;
import java.util.function.Function;

public class GamesController extends Controller<User> {
    public GamesController(Main main, Function<Wordcrex, User> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update((model) -> model.poll(UserPoll.GAMES));
    }

    @Override
    public View<? extends Controller<User>> createView() {
        return new GamesView(this);
    }

    public boolean isCurrentUser(String username) {
        return username.equals(this.getModel().username);
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
        if ((game.state == GameState.PENDING && game.inviteState == InviteState.ACCEPTED) || (game.state == GameState.PLAYING && game.getLastRound() == null)) {
            return "Wachten op bevestiging - ";
        } else if (game.state == GameState.PLAYING) {
            return (this.isCurrentUser(game.host) == (game.getLastRound().hostTurn == null)) || (this.isCurrentUser(game.opponent) == (game.getLastRound().opponentTurn == null)) ? "Jouw beurt - " : "Beurt van de tegenstander - ";
        } else if (game.state == GameState.FINISHED || game.state == GameState.RESIGNED) {
            return game.winner.equals(this.getModel().username) ? "Je hebt gewonnen - " : "Je hebt verloren - ";
        }

        return "";
    }

    public List<Game> getGames() {
        return this.getModel().games;
    }
}
