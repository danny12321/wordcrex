package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.model.Character;

import java.util.List;
import java.util.function.Function;

public class ObserveGameController extends AbstractGameController {
    private Game game;

    public ObserveGameController(Main main, Function<User, Game> fn) {
        super(main, fn);
        this.game = this.getModel().initialize();
    }

    @Override
    public void poll() {
        this.game = this.game.poll();
    }

    @Override
    public String getScore() {
        return this.game.getHostScore() + " - " + this.game.getOpponentScore();
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
    public List<Tile> getTiles() {
        return this.game.tiles;
    }

    @Override
    public List<Played> getBoard() {
        return this.game.getLastRound().board;
    }

    @Override
    public List<Character> getDeck() {
        if (this.game.state != GameState.PLAYING) {
            return List.of();
        }

        return this.game.getLastRound().characters;
    }

    @Override
    public int getPoolSize() {
        return this.game.pool.size();
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
}
