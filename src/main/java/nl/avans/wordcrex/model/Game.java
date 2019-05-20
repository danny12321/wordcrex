package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.Collections;
import java.util.stream.Collectors;

public class Game implements Pollable<Game> {
    private final Database database;

    public final int id;
    public final boolean turn;
    public final User host;
    public final User opponent;
    public final GameState state;
    public final InviteState inviteState;

    public Game(Game game, boolean turn, GameState state, InviteState inviteState) {
        this(game.database, game.id, turn, game.host, game.opponent, state, inviteState);
    }

    public Game(Database database, int id, boolean turn, User host, User opponent, GameState state, InviteState inviteState) {
        this.database = database;
        this.id = id;
        this.turn = turn;
        this.host = host;
        this.opponent = opponent;
        this.state = state;
        this.inviteState = inviteState;
    }

    @Override
    public Game poll() {
        var ref = new Object() {
            boolean turn;
            GameState state;
            InviteState inviteState;
        };
        this.database.select(
            "SELECT g.game_state, g.answer_player2, isnull((SELECT p.username_player1 FROM turnplayer1 p WHERE p.game_id = g.game_id AND p.turn_id = (SELECT max(t.turn_id)))) turn FROM game g JOIN turn t ON g.game_id = t.game_id WHERE g.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                ref.turn = result.getBoolean("turn");
                ref.state = GameState.byState(result.getString("game_state"));
                ref.inviteState = InviteState.byState(result.getString("answer_player2"));
            }
        );

        return new Game(this, ref.turn, ref.state, ref.inviteState);
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
