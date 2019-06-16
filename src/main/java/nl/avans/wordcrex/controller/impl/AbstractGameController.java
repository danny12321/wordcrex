package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Character;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.BoardView;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.GameView;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractGameController extends Controller<Game> {
    private BoardView view = BoardView.WINNER;

    public AbstractGameController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update(Game::poll);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new GameView(this);
    }

    public abstract boolean canPlay();

    public List<Tile> getTiles() {
        return this.getRoot().tiles;
    }

    public List<Played> getBoard() {
        return this.getModel().getBoard(this.getRound().id);
    }

    public boolean isHost() {
        return this.getRoot().user.username.equals(this.getHost());
    }

    public abstract List<Playable> getDeck();

    public abstract void shuffle();

    public abstract List<Played> getPlayed();

    public abstract void setPlayed(List<Played> played);

    public abstract Round getRound();

    public abstract int getPool();

    public String getWinner() {
        var winner = this.getModel().winner;

        return winner != null ? winner : "";
    }

    public String getHost() {
        return this.getModel().host;
    }

    public String getOpponent() {
        return this.getModel().opponent;
    }

    public abstract String getFormattedScore();

    public int getTotalRounds() {
        return this.getModel().rounds.size();
    }

    public abstract void nextRound();

    public abstract void previousRound();

    public Color getTileColor(Tile tile) {
        switch (tile.type) {
            case NONE:
                return Colors.DARKERER_BLUE;
            case CENTER:
                return Colors.DARK_YELLOW;
            case LETTER:
                return tile.multiplier == 2 ? Colors.TILE_2L : tile.multiplier == 4 ? Colors.TILE_4L : Colors.TILE_6L;
            case WORD:
                return tile.multiplier == 3 ? Colors.TILE_3W : Colors.TILE_4W;
            default:
                return Color.BLACK;
        }
    }

    public int getScore() {
        return this.getModel().getScore(this.getBoard(), this.getPlayed(), !this.main.debug);
    }

    public void setView(BoardView view) {
        this.view = view;
    }

    public BoardView getView() {
        return this.view;
    }

    public boolean hasWon() {
        return this.getRoot().user.username.equals(this.getModel().winner);
    }

    public abstract void play();

    public abstract void resign();

    public abstract void navigateHistory();

    public abstract void navigateChat();
}
