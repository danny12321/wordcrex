package nl.avans.wordcrex.model;

public class Word {
    public final String word;
    public final String username;
    public final WordState state;
    public final Dictionary dictionary;

    public Word(String word, String username, WordState state, Dictionary dictionary) {
        this.word = word;
        this.username = username;
        this.state = state;
        this.dictionary = dictionary;
    }
}
