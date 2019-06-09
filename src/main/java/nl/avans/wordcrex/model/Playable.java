package nl.avans.wordcrex.model;

public class Playable {
    public final int id;
    public final boolean available;
    public final Character character;

    public Playable(int id, boolean available, Character character) {
        this.id = id;
        this.available = available;
        this.character = character;
    }
}
