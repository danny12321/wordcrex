package nl.avans.wordcrex.model;

public class Word {
    public final String word;
    public final WordState state;
    public final String username;
    public final Dictionary dictionary;

    public Word(String word, WordState state, String username, Dictionary dictionary) {
        this.word = word;
        this.state = state;
        this.username = username;
        this.dictionary = dictionary;
    }
}
