package nl.avans.wordcrex.controller.impl;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.controller.Controller;
import nl.avans.wordcrex.model.Game;
import nl.avans.wordcrex.model.Round;
import nl.avans.wordcrex.model.Turn;
import nl.avans.wordcrex.model.Wordcrex;
import nl.avans.wordcrex.util.ListUtil;
import nl.avans.wordcrex.view.View;
import nl.avans.wordcrex.view.impl.HistoryView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HistoryController extends Controller<Game> {
    private Map<String, String> played = new HashMap<>();

    public HistoryController(Main main, Function<Wordcrex, Game> fn) {
        super(main, fn);
    }

    @Override
    public void poll() {
        this.update(Game::poll);
    }

    @Override
    public View<? extends Controller<Game>> createView() {
        return new HistoryView(this);
    }

    public String getPlayedWord(Round round, Turn turn) {
        var key = round.id + ";" + turn.score + ";" + turn.bonus;

        if (this.played.containsKey(key)) {
            return this.played.get(key);
        }

        var result = this.getModel().getPlayedWord(this.getModel().getBoard(round.id), turn.played);

        this.played.put(key, result);

        return result;
    }

    public List<Round> getRounds() {
        var finishedRounds = this.getModel().rounds.stream()
            .filter((round) -> round.hostTurn != null && round.opponentTurn != null)
            .collect(Collectors.toList());

        return ListUtil.reverseList(finishedRounds);
    }

    public String getHost() {
        return this.getModel().host;
    }

    public int getHostScore() {
        var round = this.getModel().getLastRound();
        var current = round.hostTurn != null && round.opponentTurn != null ? round.hostTurn.score + round.hostTurn.bonus : 0;

        return round.hostScore + current;
    }

    public String getOpponent() {
        return this.getModel().opponent;
    }

    public int getOpponentScore() {
        var round = this.getModel().getLastRound();
        var current = round.hostTurn != null && round.opponentTurn != null ? round.opponentTurn.score + round.opponentTurn.bonus : 0;

        return round.opponentScore + current;
    }

    public String getWinner() {
        var winner = this.getModel().winner;

        return winner != null ? winner : "";
    }
}
