package nl.avans.wordcrex.model.update;

import nl.avans.wordcrex.model.Tile;

public class BoardUpdate {
    public final Tile[] tiles;

    public BoardUpdate(Tile[] tiles) {
        this.tiles = tiles;
    }
}
