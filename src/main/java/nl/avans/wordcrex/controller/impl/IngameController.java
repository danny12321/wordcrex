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
    public int getTotalRounds() {
        return this.getModel().rounds.size();
    }

    @Override
    public List<Tile> getTiles() {
        return this.getModel().tiles;
    }

    @Override
    public void previousRound() {
        throw new RuntimeException();
    }

    @Override
    public void nextRound() {
        throw new RuntimeException();
    }

    @Override
    public int getPoolSize() {
        return (int) this.getModel().pool.values().stream()
            .filter((c) -> c)
            .count();
    }

    @Override
    public void startNewRound() {
        this.getModel().startNewRound();
    }

    @Override
    public int getNewScore(List<Played> played) {
        return this.getModel().getScore(played);
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

    @Override
    public void play(List<Played> played){
        if(played.isEmpty()){
            this.getModel().playTurn(TurnAction.PASSED, played);
            return;
        }
            this.getModel().playTurn(TurnAction.PLAYED, played);
    }

    @Override
    public void resign(){
        this.getModel().playTurn(TurnAction.RESIGNED, List.of());
    }
}
