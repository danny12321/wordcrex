package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.Collections;
import java.util.stream.Collectors;

public class Game implements Pollable<Game> {
    private final Database database;

    public final int id;
    public final User host;
    public final User opponent;
    public final GameState state;
    public final InviteState inviteState;

    public Game(Game game, GameState state, InviteState inviteState) {
        this(game.database, game.id, game.host, game.opponent, state, inviteState);
    }

    public Game(Database database, int id, User host, User opponent, GameState state, InviteState inviteState) {
        this.database = database;
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.state = state;
        this.inviteState = inviteState;
    }

    @Override
    public Game poll() {
        var ref = new Object() {
            GameState state;
            InviteState inviteState;
        };
        this.database.select(
            "SELECT g.game_state, g.answer_player2 FROM game g WHERE g.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                ref.state = GameState.byState(result.getString("game_state"));
                ref.inviteState = InviteState.byState(result.getString("answer_player2"));
            }
        );

        return new Game(this, ref.state, ref.inviteState);
    }

    @Override
    public User persist(User user) {
        var matches = user.games.stream()
            .map((match) -> match.id == this.id ? this : match)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        return new User(user, user.roles, matches);
    }

    public User getAuthenticatedUser() {
        return this.host.authenticated ? this.host : this.opponent;
    }
}
