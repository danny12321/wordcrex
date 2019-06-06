package nl.avans.wordcrex.model;

public enum TileSide {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    public final int x;
    public final int y;

    TileSide(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
