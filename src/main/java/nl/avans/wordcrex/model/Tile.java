package nl.avans.wordcrex.model;

public class Tile {
    public final int x;
    public final int y;
    public final Multiplier multiplier;

    public Tile(int x, int y, Multiplier multiplier) {
        this.x = x;
        this.y = y;
        this.multiplier = multiplier;
    }
}
