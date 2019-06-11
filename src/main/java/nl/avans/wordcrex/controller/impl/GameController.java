package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.StreamUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameController extends AbstractGameController {
    private List<Playable> deck = new ArrayList<>();
    private List<Played> played = new ArrayList<>();

    public GameController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public List<Playable> getDeck() {
        var next = this.getRound().deck;
        var ids = this.deck.stream().map((d) -> d.id).collect(Collectors.toList());

        if (this.deck.size() != next.size() || !next.stream().allMatch((n) -> ids.contains(n.id))) {
            this.deck = List.copyOf(next);
        }

        return this.deck;
    }

    @Override
    public void shuffle() {
        var deck = new ArrayList<>(this.deck);

        Collections.shuffle(deck);

        this.deck = List.copyOf(deck);
    }

    @Override
    public List<Played> getPlayed() {
        var round = this.getRound();
        var turn = this.isHost() ? round.hostTurn : round.opponentTurn;

        if (turn != null) {
            return List.of();
        }

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
    }

    @Override
    public void previousRound() {
    }

    @Override
    public void play() {
        if (this.getScore() <= 0 && !this.played.isEmpty()) {
            return;
        }

        this.getModel().playTurn(this.getRoot().user.username, this.getPlayed());
    }

    @Override
    public void resign() {
        this.getModel().resign(this.getRoot().user.username);
    }

    @Override
    public void navigateHistory() {
        this.main.openController(HistoryController.class, StreamUtil.getModelProperty((model) -> model.user.games, (game) -> game.id == this.getModel().id));
    }

    @Override
    public void navigateChat() {
        this.main.openController(ChatController.class, StreamUtil.getModelProperty((model) -> model.user.games, (game) -> game.id == this.getModel().id));
    }
}
