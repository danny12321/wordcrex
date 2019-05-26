package nl.avans.wordcrex.model;

public class Word {
    public final String word;
    public final WordState state;
    public final String username;

    public Word(String word, WordState state, String username) {
        this.word = word;
        this.state = state;
        this.username = username;
    }
}
