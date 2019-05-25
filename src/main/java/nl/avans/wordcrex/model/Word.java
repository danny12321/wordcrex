package nl.avans.wordcrex.model;

public class Word {
    public final String word;
    public final WordState state;
    public final User user;

    public Word(String word, WordState state, User user) {
        this.word = word;
        this.state = state;
        this.user = user;
    }
}
