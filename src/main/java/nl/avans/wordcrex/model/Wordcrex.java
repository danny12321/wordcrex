package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Persistable;

import java.util.ArrayList;
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
        var tiles = new ArrayList<Tile>();

        database.select(
            "SELECT t.x, t.y, t.tile_type type FROM tile t",
            (result) -> {
                var x = result.getInt("x");
                var y = result.getInt("y");
                var type = TileType.byType(result.getString("type"));

                tiles.add(new Tile(x, y, type.a, type.b));
            }
        );

        var dictionaries = new ArrayList<Dictionary>();

        database.select(
            "SELECT d.code id, d.description name, group_concat(c.symbol) characters, group_concat(c.value) `values`, group_concat(c.counted) `amounts` FROM letterset d JOIN symbol c ON d.code = c.letterset_code GROUP BY id",
            (result) -> {
                var id = result.getString("id");
                var name = result.getString("name");
                var characters = new ArrayList<Character>();

                var charactersRaw = result.getString("characters").split(",");
                var valuesRaw = result.getString("values").split(",");
                var amountsRaw = result.getString("amounts").split(",");

                for (var i = 0; i < charactersRaw.length; i++) {
                    characters.add(new Character(charactersRaw[i], Integer.parseInt(valuesRaw[i]), Integer.parseInt(amountsRaw[i])));
                }

                dictionaries.add(new Dictionary(database, id, name, List.copyOf(characters)));
            }
        );

        return new Wordcrex(database, null, List.copyOf(tiles), List.copyOf(dictionaries));
    }

    @Override
    public Wordcrex persist() {
        return this;
    }

    public Wordcrex register(String username, String password) {
        throw new RuntimeException();
    }

    public Wordcrex login(String username, String password) {
        throw new RuntimeException();
    }

    public Wordcrex logout() {
        return new Wordcrex(this.database, null, this.tiles, this.dictionaries);
    }
}
