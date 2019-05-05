package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Round {
    private final Database database;
    private final Model model;

    public final int id;
    public final int round;
    public final Match match;
    public final List<Character> deck;

    public Round(Database database, Model model, int id, int round, Match match, List<Character> deck) {
        this.database = database;
        this.model = model;
        this.id = id;
        this.round = round;
        this.match = match;
        this.deck = deck;
    }

    public Map<Integer, Character> getCharacters() {
        var characters = new HashMap<Integer, Character>();

        this.database.select(
            "SELECT c.character, c.position FROM turn t JOIN round r ON t.round_id = r.id JOIN `character` c on t.id = c.turn_id WHERE r.match_id = ? AND t.score = (SELECT max(t1.score) FROM turn t1 WHERE t1.round_id = r.id)",
            (statement) -> statement.setInt(1, this.match.id),
            (result) -> characters.put(result.getInt("position"), Character.byCharacter(result.getString("character")))
        );

        return Map.copyOf(characters);
    }

    public void playTurn(int score, Map<Integer, Character> played) {
        var id = this.database.insert(
            "INSERT INTO turn (user_id, round_id, date, score) VALUES (?, ?, ?, ?)",
            (statement) -> {
                statement.setInt(1, this.model.getUser().id);
                statement.setInt(2, this.id);
                statement.setTimestamp(3, new Timestamp(new Date().getTime()));
                statement.setInt(4, score);
            }
        );

        if (id == -1) {
            return;
        }

        played.forEach((key, value) -> this.database.insert(
            "INSERT INTO `character` (turn_id, `character`, position) VALUES (?, ?, ?)",
            (statement) -> {
                statement.setInt(1, id);
                statement.setString(2, String.valueOf(value.character));
                statement.setInt(3, key);
            }
        ));
    }
}
