package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Match implements Pollable<Match> {
    private final Database database;

    public final int id;
    public final User host;
    public final User opponent;
    public final Status status;
    public final List<Round> rounds;

    public Match(Database database, int id, User host, User opponent, Status status) {
        this(database, id, host, opponent, status, List.of());
    }

    public Match(Match match, Status status, List<Round> rounds) {
        this(match.database, match.id, match.host, match.opponent, status, rounds);
    }

    public Match(Database database, int id, User host, User opponent, Status status, List<Round> rounds) {
        this.database = database;
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.status = status;
        this.rounds = rounds;
    }

    @Override
    public Match poll() {
        var ref = new Object() {
            Status status;
        };
        this.database.select(
            "SELECT m.status FROM `match` m WHERE m.id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> ref.status = Status.byStatus(result.getInt("status"))
        );

        var rounds = new ArrayList<Round>();

        this.database.select(
            "SELECT r.id, r.round, r.deck FROM round r WHERE r.match_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                var deck = new ArrayList<Character>();
                var string = result.getString("deck").split("");

                for (var part : string) {
                    deck.add(Character.byCharacter(part));
                }

                rounds.add(new Round(this.database, result.getInt("id"), result.getInt("round"), this, List.copyOf(deck)));
            }
        );

        return new Match(this, ref.status, List.copyOf(rounds));
    }

    @Override
    public User persist(User user) {
        var matches = user.matches.stream()
            .map((match) -> match.id == this.id ? this : match)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        return new User(user, user.roles, matches);
    }

    public User getAuthenticatedUser() {
        return this.host.isAuthenticated() ? this.host : this.opponent;
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
