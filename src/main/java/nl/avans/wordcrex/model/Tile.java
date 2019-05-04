package nl.avans.wordcrex.model;

public class Tile {
    public final Multiplier multiplier;

    private Character character = null;

    public Tile(Multiplier multiplier) {
        this.multiplier = multiplier;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
}
