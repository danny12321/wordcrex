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

public class GameController extends AbstractGameController {
    private List<Played> played = new ArrayList<>();

    public GameController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public List<Played> getPlayed() {
        return this.played;
    }

    @Override
    public void setPlayed(List<Played> played) {
        this.played = List.copyOf(played);
    }

    @Override
    public Round getRound() {
        return this.getModel().getLastRound();
    }

    @Override
    public int getPool() {
        return (int) this.getModel().pool.stream()
            .filter((c) -> c.available)
            .count();
    }

    @Override
    public String getFormattedScore() {
        var round = this.getRound();

        return round.hostScore + " - " + round.opponentScore;
    }

    @Override
    public void nextRound() {
        throw new RuntimeException();
    }

    @Override
    public void previousRound() {
        throw new RuntimeException();
    }

    @Override
    public void navigateHistory() {
        this.main.openController(HistoryController.class, StreamUtil.getModelProperty((model) -> model.user.games, (game) -> game.id == this.getModel().id));
    }
}
