package nl.avans.wordcrex.model;

public class Tile {
    public final int x;
    public final int y;
    public final TileType type;
    public final int multiplier;

    public Tile(int x, int y, TileType type, int multiplier) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.multiplier = multiplier;
    }
}
