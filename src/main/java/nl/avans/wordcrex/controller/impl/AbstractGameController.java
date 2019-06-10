package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.util.BoardView;
import nl.avans.wordcrex.util.Colors;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.GameView;

import java.awt.*;
import java.util.ArrayList;
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
        var board = new ArrayList<Played>();

        for (var round : this.getModel().rounds) {
            if (round == this.getRound()) {
                break;
            }

            board.addAll(round.board);
        }

        return List.copyOf(board);
    }

    public abstract List<Played> getPlayed();

    public abstract Round getRound();

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
                return tile.multiplier == 2 ? Colors.DARK_CYAN : tile.multiplier == 4 ? Colors.DARKER_CYAN : Colors.DARKERER_CYAN;
            case WORD:
                return tile.multiplier == 3 ? Colors.DARK_PURPLE : Colors.DARKER_PURPLE;
            default:
                return Color.BLACK;
        }
    }

    public int getScore() {
        return this.getModel().getScore(this.getBoard(), this.getPlayed());
    }

    public abstract void navigateHistory();

    public void setView(BoardView view) {
        this.view = view;
    }

    public BoardView getView() {
        return view;
    }
}
