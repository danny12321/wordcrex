package nl.avans.wordcrex.model;

import nl.avans.wordcrex.Observable;
import nl.avans.wordcrex.model.update.BoardUpdate;

import java.util.Arrays;

public class Board extends Observable<BoardUpdate> {
    public final int size;

    private final Tile[] tiles;

    public Board(int size) {
        this.size = size;
        this.tiles = new Tile[size * size];

        for (var i = 0; i < size * size; i++) {
            var x = i % size;
            var y = i / size;

            this.tiles[i] = new Tile(x == 0 && y == 0 ? Multiplier.W4 : null);
        }

        this.update();
    }

    public Tile getTile(int x, int y) {
        var i = x + this.size * y;

        if (i > this.tiles.length) {
            return null;
        }

        return this.tiles[i];
    }

    private void update() {
        this.next(new BoardUpdate(Arrays.copyOf(this.tiles, this.tiles.length)));
    }
}
