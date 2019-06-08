package nl.avans.wordcrex.model;

import nl.avans.wordcrex.util.Persistable;

import java.util.List;

public class Game implements Persistable {
    public final int id;
    public final String host;
    public final String opponent;
    public final String winner;
    public final GameState state;
    public final InviteState inviteState;
    public final Dictionary dictionary;
    public final List<Playable> pool;
    public final List<Round> rounds;
    public final List<Message> messages;

    public Game(int id, String host, String opponent, String winner, GameState state, InviteState inviteState, Dictionary dictionary, List<Playable> pool, List<Round> rounds, List<Message> messages) {
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.winner = winner;
        this.state = state;
        this.inviteState = inviteState;
        this.dictionary = dictionary;
        this.pool = pool;
        this.rounds = rounds;
        this.messages = messages;
    }

    @Override
    public Wordcrex persist() {
        throw new RuntimeException();
    }

    public Game poll(GamePoll poll) {
        throw new RuntimeException();
    }

    public Round getLastRound() {
        throw new RuntimeException();
    }

    public void sendMessage(String username, String message) {
        throw new RuntimeException();
    }

    public int getScore(List<Played> played) {
        throw new RuntimeException();
    }

    public void playTurn(String username, List<Played> played) {
        throw new RuntimeException();
    }

    public void resign(String username) {
        throw new RuntimeException();
    }
}
