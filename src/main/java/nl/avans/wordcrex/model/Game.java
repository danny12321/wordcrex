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
    public final List<Tile> tiles;

    public Game(Game game, List<Tile> tiles) {
        this(game.database, game.id, game.turn, game.host, game.opponent, game.state, game.inviteState, tiles);
    }

    public Game(Game game, boolean turn, GameState state, InviteState inviteState) {
        this(game.database, game.id, turn, game.host, game.opponent, state, inviteState, game.tiles);
    }

    public Game(Database database, int id, boolean turn, User host, User opponent, GameState state, InviteState inviteState) {
        this(database, id, turn, host, opponent, state, inviteState, List.of());
    }

    public Game(Database database, int id, boolean turn, User host, User opponent, GameState state, InviteState inviteState, List<Tile> tiles) {
        this.database = database;
        this.id = id;
        this.turn = turn;
        this.host = host;
        this.opponent = opponent;
        this.state = state;
        this.inviteState = inviteState;
        this.tiles = tiles;
    }

    @Override
    public Game initialize() {
        var tiles = new ArrayList<Tile>();

        this.database.select(
            "SELECT t.x, t.y, t.tile_type FROM tile t",
            (statement) -> {},
            (result) -> {
                var rawMultiplier = result.getString("multiplier");
                Multiplier multiplier = null;

                if (rawMultiplier.length() == 2) {
                    multiplier = new Multiplier(rawMultiplier);
                }

                tiles.add(new Tile(result.getInt("x"), result.getInt("y"), multiplier));
            }
        );

        return new Game(this, List.copyOf(tiles));
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
