package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.StreamUtil;

import java.util.List;
import java.util.function.Function;

public class GameController extends AbstractGameController {
    public GameController(Main main, Function<User, Game> fn) {
        super(main, fn);
    }

    @Override
    public String getScore() {
        return this.getModel().getHostScore() + " - " + this.getModel().getOpponentScore();
    }

    @Override
    public String getHostName() {
        return this.getModel().host;
    }

    @Override
    public String getOpponentName() {
        return this.getModel().opponent;
    }

    @Override
    public List<Tile> getTiles() {
        return this.getModel().tiles;
    }

    @Override
    public List<Played> getBoard() {
        return this.getModel().getLastRound().board;
    }

    @Override
    public List<Character> getDeck() {
        if (this.getModel().state != GameState.PLAYING) {
            return List.of();
        }

        return this.getModel().getLastRound().characters;
    }

    @Override
    public int getPoolSize() {
        return this.getModel().pool.size();
    }

    @Override
    public void startNewRound() {
        this.getModel().startNewRound();
    }

    @Override
    public int getNewScore(List<Played> played) {
        return this.getModel().getLastRound().getScore(this.getTiles(), played, this.getModel().dictionary);
    }

    @Override
    public Character getPlaceholder() {
        return this.getModel().dictionary.characters.get(0);
    }

    @Override
    public void navigateChat() {
        this.main.openController(ChatController.class, StreamUtil.getModelProperty((model) -> model.games, (game) -> game.id == this.getModel().id));
    }

    @Override
    public void navigateHistory() {
        this.main.openController(HistoryController.class, StreamUtil.getModelProperty((model) -> model.games, (game) -> game.id == this.getModel().id));
    }
}
