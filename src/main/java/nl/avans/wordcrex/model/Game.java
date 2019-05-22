package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Game implements Pollable<Game> {
    private final Database database;

    public final int id;
    public final boolean turn;
    public final User host;
    public final User opponent;
    public final GameState state;
    public final InviteState inviteState;
    public final int hostScore;
    public final int opponentScore;
    public final List<Tile> tiles;

    public Game(Game game, List<Tile> tiles) {
        this(game.database, game.id, game.turn, game.host, game.opponent, game.state, game.inviteState, game.hostScore, game.opponentScore, tiles);
    }

    public Game(Game game, boolean turn, GameState state, InviteState inviteState, int hostScore, int opponentScore) {
        this(game.database, game.id, turn, game.host, game.opponent, state, inviteState, hostScore, opponentScore, game.tiles);
    }

    public Game(Database database, int id, boolean turn, User host, User opponent, GameState state, InviteState inviteState) {
        this(database, id, turn, host, opponent, state, inviteState, 0, 0, List.of());
    }

    public Game(Database database, int id, boolean turn, User host, User opponent, GameState state, InviteState inviteState, int hostScore, int opponentScore, List<Tile> tiles) {
        this.database = database;
        this.id = id;
        this.turn = turn;
        this.host = host;
        this.opponent = opponent;
        this.state = state;
        this.inviteState = inviteState;
        this.hostScore = hostScore;
        this.opponentScore = opponentScore;
        this.tiles = tiles;
    }

    @Override
    public Game initialize() {
        var tiles = new ArrayList<Tile>();

        this.database.select(
            "SELECT t.x, t.y, t.tile_type FROM tile t",
            (statement) -> {},
            (result) -> tiles.add(new Tile(result.getInt("x"), result.getInt("y"), result.getString("tile_type")))
        );

        return new Game(this, List.copyOf(tiles));
    }

    @Override
    public Game poll() {
        var ref = new Object() {
            boolean turn;
            GameState state;
            InviteState inviteState;
            int hostScore;
            int opponentScore;
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
        this.database.select(
            "SELECT s.score1 + s.bonus1 host, s.score2 + s.bonus2 opponent FROM score s WHERE s.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                ref.hostScore = result.getInt("host");
                ref.opponentScore = result.getInt("opponent");
            }
        );

        return new Game(this, ref.turn, ref.state, ref.inviteState, ref.hostScore, ref.opponentScore);
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
