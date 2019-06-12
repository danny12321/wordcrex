package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary {
    private final Database database;
    private final Map<String, Boolean> cache = new HashMap<>();

    public final String id;
    public final String name;
    public final List<Character> characters;

    public Dictionary(Database database, String id, String name, List<Character> characters) {
        this.database = database;
        this.id = id;
        this.name = name;
        this.characters = characters;
    }

    public boolean isWord(String word) {
        if (this.cache.containsKey(word)) {
            return this.cache.get(word);
        }

        var valid = this.database.select(
            "SELECT d.word FROM dictionary d WHERE d.letterset_code = ? AND d.word = ? AND d.state = ?",
            (statement) -> {
                statement.setString(1, this.id);
                statement.setString(2, word);
                statement.setString(3, WordState.ACCEPTED.state);
            },
            (result) -> {}
        ) > 0;

        this.cache.put(word, valid);

        return valid;
    }
}
