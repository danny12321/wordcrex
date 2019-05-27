package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;

import java.util.List;

public class Dictionary {
    private final Database database;

    public final String code;
    public final String description;
    public final List<Character> characters;

    public Dictionary(Database database, String code, String description, List<Character> characters) {
        this.database = database;
        this.code = code;
        this.description = description;
        this.characters = characters;
    }

    public boolean isWord(String word) {
        return this.database.select(
            "SELECT d.word FROM dictionary d WHERE d.letterset_code = ? AND d.word = ? AND d.state = ?",
            (statement) -> {
                statement.setString(1, this.code);
                statement.setString(2, word);
                statement.setString(3, WordState.ACCEPTED.state);
            },
            (result) -> {}
        ) > 0;
    }
}
