package nl.avans.wordcrex.model;

import java.util.stream.Stream;

public enum TileSide {
    NORTH(0, -1, TileAxis.VERTICAL),
    EAST(1, 0, TileAxis.HORIZONTAL),
    SOUTH(0, 1, TileAxis.VERTICAL),
    WEST(-1, 0, TileAxis.HORIZONTAL);

    public final int x;
    public final int y;
    public final TileAxis axis;

    TileSide(int x, int y, TileAxis axis) {
        this.x = x;
        this.y = y;
        this.axis = axis;
    }

    public static TileSide[] ofAxis(TileAxis axis) {
        return Stream.of(TileSide.values())
            .filter((t) -> t.axis == axis)
            .toArray(TileSide[]::new);
    }
}
