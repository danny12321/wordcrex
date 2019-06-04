package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.StreamUtil;

import java.util.List;
import java.util.function.Function;

public class IngameController extends GameController {
    public IngameController(Main main, Function<User, Game> fn) {
        super(main, fn);
    }

    @Override
    public boolean canPlay() {
        return true;
    }

    @Override
    public String getScore() {
        return this.getRound().hostScore + " - " + this.getRound().opponentScore;
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
    public Round getRound() {
        return this.getModel().getLastRound();
    }

    @Override
    public List<Tile> getTiles() {
        return this.getModel().tiles;
    }

    @Override
    public boolean previousRound() {
        throw new RuntimeException();
    }

    @Override
    public boolean nextRound() {
        throw new RuntimeException();
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
        return this.getRound().getScore(this.getModel().tiles, played, this.getModel().dictionary);
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
