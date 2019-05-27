package nl.avans.wordcrex.model;

import java.util.List;

public class Dictionary {
    public final String code;
    public final String description;
    public final List<Character> characters;
    public final List<Word> words;

    public Dictionary(String code, String description, List<Character> characters, List<Word> words) {
        this.code = code;
        this.description = description;
        this.characters = characters;
        this.words = words;
    }
}
