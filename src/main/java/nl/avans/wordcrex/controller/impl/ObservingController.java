package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.BoardView;
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
    public void poll() {
        var current = this.getModel().getLastRound().id;

        super.poll();

        var next = this.getModel().getLastRound().id;

        if (next != current && this.round == current - 1) {
            this.round = next - 1;
        }
    }

    @Override
    public boolean canPlay() {
        return false;
    }

    @Override
    public List<Played> getBoard() {
        var board = new ArrayList<>(super.getBoard());

        board.addAll(this.getPlayed());

        return List.copyOf(board);
    }

    @Override
    public List<Playable> getDeck() {
        return this.getRound().deck;
    }

    @Override
    public void shuffle() {
    }

    @Override
    public List<Played> getPlayed() {
        var round = this.getRound();

        if (this.getView() == BoardView.HOST) {
            return round.hostTurn != null ? round.hostTurn.played : List.of();
        } else if (this.getView() == BoardView.OPPONENT) {
            return round.opponentTurn != null ? round.opponentTurn.played : List.of();
        }

        return round.board;
    }

    @Override
    public void setPlayed(List<Played> played) {
    }

    @Override
    public Round getRound() {
        return this.getModel().rounds.get(this.round);
    }

    @Override
    public int getPool() {
        return this.getModel().pool.size() - super.getBoard().size() - this.getRound().deck.size();
    }

    @Override
    public String getFormattedScore() {
        var round = this.getRound();
        var hostCurrent = round.hostTurn != null && round.opponentTurn != null ? round.hostTurn.score + round.hostTurn.bonus : 0;
        var opponentCurrent = round.hostTurn != null && round.opponentTurn != null ? round.opponentTurn.score + round.opponentTurn.bonus : 0;

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
        return this.getModel().getScore(super.getBoard(), this.getPlayed(), !this.main.debug);
    }

    @Override
    public void play() {
    }

    @Override
    public void resign() {
    }

    @Override
    public void navigateHistory() {
        this.main.openController(HistoryController.class, StreamUtil.getModelProperty((model) -> model.user.observable, (game) -> game.id == this.getModel().id));
    }

    @Override
    public void navigateChat() {
    }
}
