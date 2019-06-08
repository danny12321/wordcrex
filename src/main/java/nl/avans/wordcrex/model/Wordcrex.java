package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Persistable;

import java.util.List;

public class Wordcrex implements Persistable {
    private final Database database;

    public final User user;
    public final List<Tile> tiles;
    public final List<Dictionary> dictionaries;

    public Wordcrex(Database database, User user, List<Tile> tiles, List<Dictionary> dictionaries) {
        this.database = database;
        this.user = user;
        this.tiles = tiles;
        this.dictionaries = dictionaries;
    }

    public static Wordcrex initialize(Database database) {
        throw new RuntimeException();
    }

    @Override
    public Wordcrex persist() {
        throw new RuntimeException();
    }

    public Wordcrex register(String username, String password) {
        throw new RuntimeException();
    }

    public Wordcrex login(String username, String password) {
        throw new RuntimeException();
    }

    public Wordcrex logout() {
        throw new RuntimeException();
    }
}
