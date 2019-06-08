package nl.avans.wordcrex.model;

public class Word {
    public final String word;
    public final String username;
    public final WordState state;

    public Word(String word, String username, WordState state) {
        this.word = word;
        this.username = username;
        this.state = state;
    }
}
