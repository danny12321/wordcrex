package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Persistable;

import java.util.List;

public class User implements Persistable {
    private final Database database;

    public final List<UserRole> roles;
    public final List<Word> words;
    public final List<Game> games;
    public final List<Game> observable;
    public final List<User> manageable;

    public User(Database database, List<UserRole> roles, List<Word> words, List<Game> games, List<Game> observable, List<User> manageable) {
        this.database = database;
        this.roles = roles;
        this.words = words;
        this.games = games;
        this.observable = observable;
        this.manageable = manageable;
    }

    @Override
    public Wordcrex persist() {
        throw new RuntimeException();
    }

    public User poll(UserPoll poll) {
        throw new RuntimeException();
    }

    public boolean hasRole(UserRole role) {
        throw new RuntimeException();
    }

    public List<String> findOpponents(String username) {
        throw new RuntimeException();
    }

    public void sendInvite(String username, Dictionary dictionary) {
        throw new RuntimeException();
    }

    public void respondInvite(Game game, GameState state) {
        throw new RuntimeException();
    }

    public void suggestWord(String word, Dictionary dictionary) {
        throw new RuntimeException();
    }

    public void respondSuggestion(Word word, WordState state) {
        throw new RuntimeException();
    }

    public void changePassword(String password) {
        throw new RuntimeException();
    }

    public void setRoles(String username, List<UserRole> roles) {
        throw new RuntimeException();
    }
}
