package nl.avans.wordcrex.model;

public class Played {
    public final Letter letter;
    public final int x;
    public final int y;

    public Played(Letter letter, int x, int y) {
        this.letter = letter;
        this.x = x;
        this.y = y;
    }
}
