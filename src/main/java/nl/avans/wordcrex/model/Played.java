package nl.avans.wordcrex.model;

public class Played {
    public final Playable playable;
    public final Tile tile;

    public Played(Playable playable, Tile tile) {
        this.playable = playable;
        this.tile = tile;
    }
}
