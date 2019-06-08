package nl.avans.wordcrex.model;

public class Playable {
    public final int id;
    public final boolean played;
    public final Character character;

    public Playable(int id, boolean played, Character character) {
        this.id = id;
        this.played = played;
        this.character = character;
    }
}
