package nl.avans.wordcrex.model;

import nl.avans.wordcrex.Observable;
import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.model.update.MatchUpdate;

import java.util.ArrayList;
import java.util.List;

public class Match extends Observable<MatchUpdate> {
    private final Database database;

    public final int id;
    public final Player host;
    public final Player opponent;
    public final Status status;

    public Match(Database database, int id, Player host, Player opponent, Status status) {
        super(new MatchUpdate(List.of()));
        this.database = database;
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.status = status;
    }

    public void poll() {
        var last = this.getLast();
        var rounds = new ArrayList<Round>();

        var selected = this.database.select(
            "SELECT r.id, r.round, r.deck FROM round r WHERE r.match_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                var deck = new ArrayList<Character>();
                var string = result.getString("deck").split("");

                for (var part : string) {
                    deck.add(Character.byCharacter(part));
                }

                rounds.add(new Round(result.getInt("id"), result.getInt("round"), List.copyOf(deck)));
            }
        );

        if (selected == last.rounds.size()) {
            return;
        }

        this.next(new MatchUpdate(List.copyOf(rounds)));
    }

    public void setStatus(Status status) {
        if (status == null) {
            return;
        }

        this.database.update(
            "UPDATE `match` SET status = ? WHERE id = ?",
            (statement) -> {
                statement.setInt(1, status.status);
                statement.setInt(2, this.id);
            }
        );
    }

    public enum Status {
        PENDING("INVITES", 0),
        PLAYING("PLAYING", 1),
        OVER("GAME OVER", 2),
        REJECTED("", 3);

        public final String name;
        public final int status;

        Status(String name, int status) {
            this.name = name;
            this.status = status;
        }

        public static Status byStatus(int status) {
            for (var s : Status.values()) {
                if (s.status == status) {
                    return s;
                }
            }

            return null;
        }
    }
}
