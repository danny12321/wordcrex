package nl.avans.wordcrex.model;

public class Letter {
    public final int id;
    public final Dictionary dictionary;
    public final Character character;

    public Letter(int id, Dictionary dictionary, Character character) {
        this.id = id;
        this.dictionary = dictionary;
        this.character = character;
    }
}
