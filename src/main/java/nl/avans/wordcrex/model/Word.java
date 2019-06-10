package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.ListUtil;
import nl.avans.wordcrex.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class Word {
    public final String word;
    public final String username;
    public final WordState state;
    public final Dictionary dictionary;

    public Word(String word, String username, WordState state, Dictionary dictionary) {
        this.word = word;
        this.username = username;
        this.state = state;
        this.dictionary = dictionary;
    }

    public static List<Word> initialize(Database database, Wordcrex wordcrex, String username, WordState... states) {
        var ref = new Object() {
            List<Word> words = new ArrayList<>();
        };

        var finalStates = states.length == 0 ? WordState.values() : states;

        database.select(
            "SELECT w.word, w.letterset_code dictionary_id, w.state, w.username FROM dictionary w WHERE w.username LIKE ? AND w.state IN (" + StringUtil.getPlaceholders(finalStates.length) + ") ORDER BY w.letterset_code LIMIT 100",
            (statement) -> {
                statement.setString(1, username.isEmpty() ? "%" : username);

                for (var i = 0; i < finalStates.length; i++) {
                    statement.setString(i + 2, finalStates[i].state);
                }
            },
            (result) -> {
                var word = result.getString("word");
                var state = WordState.byState(result.getString("state"));
                var submitter = result.getString("username");

                var dictionaryId = result.getString("dictionary_id");
                var dictionary = ListUtil.find(wordcrex.dictionaries, (d) -> d.id.equals(dictionaryId));

                ref.words.add(new Word(word, submitter, state, dictionary));
            }
        );

        return List.copyOf(ref.words);
    }
}
