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
        return false;
    }
}
