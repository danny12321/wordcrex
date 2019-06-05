package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.model.Character;

import java.util.List;
import java.util.function.Function;

public class ObserveGameController extends GameController {
    private Game game;
    private int round;

    public ObserveGameController(Main main, Function<User, Game> fn) {
        super(main, fn);
        this.game = this.getModel().initialize();
    }

    @Override
    public boolean canPlay() {
        return false;
    }

    @Override
    public void poll() {
        this.game = this.game.poll();
    }

    @Override
    public String getScore() {
        var hostTurn = this.getRound().hostTurn;
        var hostAdditional = 0;

        if (hostTurn != null) {
            hostAdditional = hostTurn.score + hostTurn.bonus;
        }
        var opponentTurn = this.getRound().opponentTurn;
        var opponentAdditional = 0;

        if (opponentTurn != null) {
            opponentAdditional = opponentTurn.score + opponentTurn.bonus;
        }

        return (hostAdditional + this.getRound().hostScore) + " - " + (opponentAdditional + this.getRound().opponentScore);
    }

    @Override
    public String getHostName() {
        return this.game.host;
    }

    @Override
    public String getOpponentName() {
        return this.game.opponent;
    }

    @Override
    public Round getRound() {
        return this.game.rounds.get(this.round);
    }

    @Override
    public int getTotalRounds() {
        return this.game.rounds.size();
    }

    @Override
    public List<Tile> getTiles() {
        return this.game.tiles;
    }

    @Override
    public void previousRound() {
        if (this.round > 0) {
            this.round--;
        }
    }

    @Override
    public void nextRound() {
        if (this.round < this.getTotalRounds() - 1) {
            this.round++;
        }
    }

    @Override
    public int getPoolSize() {
        throw new RuntimeException();
    }

    @Override
    public void startNewRound() {
        throw new RuntimeException();
    }

    @Override
    public int getNewScore(List<Played> played) {
        throw new RuntimeException();
    }

    @Override
    public Character getPlaceholder() {
        return this.game.dictionary.characters.get(0);
    }

    @Override
    public void navigateChat() {
        throw new RuntimeException();
    }

    @Override
    public void navigateHistory() {
        throw new RuntimeException();
    }

    @Override
    public void play(List<Played> played){
        throw new RuntimeException();
    }

    public void resign(List<Played> played){
        throw new RuntimeException();
    }
}
