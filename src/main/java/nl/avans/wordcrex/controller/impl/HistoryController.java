package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.ListUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.HistoryView;

import java.util.List;
import java.util.function.Function;

public class HistoryController extends Controller<Game> {
    public HistoryController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update(Game::poll);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new HistoryView(this);
    }

    public List<Round> getRounds() {
        return ListUtil.reverseList(this.getModel().rounds);
    }

    public String getHost() {
        return this.getModel().host;
    }

    public int getHostScore() {
        var round = getModel().getLastRound();
        var current = round.hostTurn != null ? round.hostTurn.score + round.hostTurn.bonus : 0;

        return round.hostScore + current;
    }

    public String getOpponent() {
        return this.getModel().opponent;
    }

    public int getOpponentScore() {
        var round = getModel().getLastRound();
        var current = round.opponentTurn != null ? round.opponentTurn.score + round.opponentTurn.bonus : 0;

        return round.opponentScore + current;
    }
}
