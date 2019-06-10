package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Played;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.StreamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ObservingController extends AbstractGameController {
    private int round;

    public ObservingController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public boolean canPlay() {
        return false;
    }

    @Override
    public List<Played> getBoard() {
        var board = new ArrayList<>(super.getBoard());
        var add = this.getRound().board;

        if (add != null) {
            board.addAll(add);
        }

        return board;
    }

    @Override
    public List<Played> getPlayed() {
        return this.getRound().board;
    }

    @Override
    public Round getRound() {
        return this.getModel().rounds.get(this.round);
    }

    @Override
    public String getFormattedScore() {
        var round = this.getRound();
        var hostCurrent = round.hostTurn != null ? round.hostTurn.score + round.hostTurn.bonus : 0;
        var opponentCurrent = round.opponentTurn != null ? round.opponentTurn.score + round.opponentTurn.bonus : 0;

        return (round.hostScore + hostCurrent) + " - " + (round.opponentScore + opponentCurrent);
    }

    @Override
    public void nextRound() {
        if (this.round < this.getTotalRounds() - 1) {
            this.round++;
        }
    }

    @Override
    public void previousRound() {
        if (this.round > 0) {
            this.round--;
        }
    }

    @Override
    public int getScore() {
        return this.getModel().getScore(super.getBoard(), this.getPlayed());
    }

    @Override
    public void navigateHistory() {
        this.main.openController(HistoryController.class, StreamUtil.getModelProperty((model) -> model.user.observable, (game) -> game.id == this.getModel().id));
    }
}
