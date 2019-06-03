package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pollable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class Game implements Pollable<Game> {
    private final Database database;

    public final int id;
    public final String host;
    public final String opponent;
    public final GameState state;
    public final InviteState inviteState;
    public final Dictionary dictionary;
    public final List<Tile> tiles;
    public final List<Letter> pool;
    public final List<Round> rounds;
    public final List<Message> messages;

    public Game(Game game, List<Tile> tiles) {
        this(game.database, game.id, game.host, game.opponent, game.state, game.inviteState, game.dictionary, tiles, game.pool, game.rounds, game.messages);
    }

    public Game(Game game, GameState state, InviteState inviteState, List<Letter> pool, List<Message> messages) {
        this(game.database, game.id, game.host, game.opponent, state, inviteState, game.dictionary, game.tiles, pool, null, messages);
    }

    public Game(Database database, int id, String host, String opponent, GameState state, InviteState inviteState, Dictionary dictionary) {
        this(database, id, host, opponent, state, inviteState, dictionary, List.of(), List.of(), null, List.of());
    }

    public Game(Database database, int id, String host, String opponent, GameState state, InviteState inviteState, Dictionary dictionary, List<Tile> tiles, List<Letter> pool, List<Round> rounds, List<Message> messages) {
        this.database = database;
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.state = state;
        this.inviteState = inviteState;
        this.dictionary = dictionary;
        this.tiles = tiles;
        this.pool = pool;
        this.rounds = rounds == null ? this.getRounds() : rounds;
        this.messages = messages;
    }

    @Override
    public Game initialize() {
        var tiles = new ArrayList<Tile>();

        this.database.select(
            "SELECT t.x, t.y, t.tile_type FROM tile t",
            (statement) -> {
            },
            (result) -> tiles.add(new Tile(result.getInt("x"), result.getInt("y"), result.getString("tile_type")))
        );

        return new Game(this.poll(), List.copyOf(tiles));
    }

    @Override
    public Game poll() {
        var ref = new Object() {
            GameState state;
            InviteState inviteState;
        };
        this.database.select(
            "SELECT g.game_state state, g.answer_player2 invite_state FROM game g WHERE g.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                ref.state = GameState.byState(result.getString("state"));
                ref.inviteState = InviteState.byState(result.getString("invite_state"));
            }
        );

        var pool = new ArrayList<Letter>();

        this.database.select(
            "SELECT p.letter_id id, p.symbol `character` FROM pot p WHERE p.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                var id = result.getInt("id");
                var symbol = result.getString("character");
                var character = this.dictionary.characters.stream()
                    .filter((c) -> c.character.equals(symbol))
                    .findFirst()
                    .orElseThrow();

                pool.add(new Letter(id, character));
            }
        );

        var messages = new ArrayList<Message>();

        this.database.select(
            "SELECT m.message, m.username, m.moment date FROM chatline m WHERE game_id = ? ORDER BY moment",
            (statement) -> statement.setInt(1, this.id),
            (result) -> messages.add(new Message(result.getString("message"), result.getString("username"), result.getDate("date")))
        );

        var game = new Game(this, ref.state, ref.inviteState, List.copyOf(pool), List.copyOf(messages));

        if (game.state == GameState.PLAYING && game.rounds.isEmpty()) {
            game.startNewRound();
        }

        return game;
    }

    private List<Round> getRounds() {
        var rounds = new ArrayList<Round>();

        this.database.select(
            "SELECT t.turn_id turn, h.turnaction_type host_action, h.score host_score, h.bonus host_bonus, hp.woorddeel host_played, hp.`x-waarden` host_x, hp.`y-waarden` host_y, o.turnaction_type opponent_action, o.score opponent_score, o.bonus opponent_bonus, op.woorddeel opponent_played, op.`x-waarden` opponent_x, op.`y-waarden` opponent_y " +
                "FROM turn t" +
                "         LEFT JOIN turnplayer1 h ON t.game_id = h.game_id AND t.turn_id = h.turn_id" +
                "         LEFT JOIN gelegdplayer1 hp ON t.game_id = hp.game_id AND t.turn_id = hp.turn_id" +
                "         LEFT JOIN turnplayer2 o ON t.game_id = o.game_id AND t.turn_id = o.turn_id" +
                "         LEFT JOIN gelegdplayer2 op ON t.game_id = op.game_id AND t.turn_id = op.turn_id " +
                "WHERE t.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                var hostTurn = this.parseTurn(result, "host");
                var opponentTurn = this.parseTurn(result, "opponent");

                rounds.add(new Round(result.getInt("turn"), hostTurn, opponentTurn));
            }
        );

        return List.copyOf(rounds);
    }

    private Turn parseTurn(ResultSet result, String player) throws SQLException {
        var action = result.getString(player + "_action");

        if (action == null) {
            return null;
        }

        var played = new ArrayList<Played>();
        var raw = result.getString(player + "_played");
        var playedX = result.getString(player + "_x");
        var playedY = result.getString(player + "_y");

        if (raw != null) {
            var rawSplitted = raw.split(",");
            var xSplitted = playedX.split(",");
            var ySplitted = playedY.split(",");

            for (var i = 0; i < rawSplitted.length; i++) {
                var index = i;

                var character = this.dictionary.characters.stream()
                    .filter((c) -> c.character.equals(rawSplitted[index]))
                    .findFirst()
                    .orElseThrow();
                var x = Integer.parseInt(xSplitted[index]);
                var y = Integer.parseInt(ySplitted[index]);

                played.add(new Played(character, x, y));
            }
        }

        return new Turn(TurnAction.byAction(action), result.getInt(player + "_score"), result.getInt(player + "_bonus"), played);
    }

    @Override
    public User persist(User user) {
        var games = user.games.stream()
            .map((match) -> match.id == this.id ? this : match)
            .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        return new User(user, user.roles, games, user.dictionaries);
    }

    public Round getLastRound() {
        if (this.rounds.size() == 0) {
            return null;
        }

        return this.rounds.get(this.rounds.size() - 1);
    }

    public int getHostScore() {
        return this.rounds.stream()
            .mapToInt((round) -> {
                if (round.hostTurn == null) {
                    return 0;
                }

                return round.hostTurn.score + round.hostTurn.bonus;
            })
            .sum();
    }

    public int getOpponentScore() {
        return this.rounds.stream()
            .mapToInt((round) -> {
                if (round.opponentTurn == null) {
                    return 0;
                }

                return round.opponentTurn.score + round.opponentTurn.bonus;
            })
            .sum();
    }

    public void startGame() {
        this.database.update(
            "UPDATE game g SET g.game_state = ? WHERE g.game_id = ?",
            (statement) -> {
                statement.setString(1, GameState.PLAYING.state);
                statement.setInt(2, this.id);
            }
        );

        var insert = new ArrayList<String>();

        this.dictionary.characters.forEach((character) -> {
            for (var i = 0; i < character.amount; i++) {
                insert.add("(?, ?, ?, ?)");
            }
        });

        this.database.insert(
            "INSERT INTO letter (letter_id, game_id, symbol_letterset_code, symbol) VALUES " + String.join(", ", insert),
            (statement) -> {
                var id = 0;
                var index = 0;

                for (var i = 0; i < this.dictionary.characters.size(); i++) {
                    var character = this.dictionary.characters.get(i);

                    for (var j = 0; j < character.amount; j++) {
                        statement.setInt(++index, ++id);
                        statement.setInt(++index, this.id);
                        statement.setString(++index, this.dictionary.code);
                        statement.setString(++index, character.character);
                    }
                }
            }
        );
    }

    public void startNewRound() {
        this.database.insert(
            "INSERT INTO turn (game_id, turn_id) VALUES (?, ?)",
            (statement) -> {
                statement.setInt(1, this.id);
                statement.setInt(2, this.rounds.size() + 1);
            }
        );

        var values = new ArrayList<String>();
        var deck = new ArrayList<Letter>();
        var size = Math.min(7, this.pool.size() - 1);
        var random = new Random();

        for (int i = 0; i < size; i++) {
            var next = random.nextInt(this.pool.size());

            deck.add(this.pool.get(next));
            values.add("(?, ?, ?)");
        }

        this.database.insert(
            "INSERT INTO handletter (game_id, turn_id, letter_id) VALUES " + String.join(", ", values),
            (statement) -> {
                var offset = 0;

                for (int i = 0; i < size; i++) {
                    statement.setInt(++offset, this.id);
                    statement.setInt(++offset, this.rounds.size() + 1);
                    statement.setInt(++offset, deck.get(i).id);
                }
            }
        );
    }

    public void sendMessage(User user, String message) {
        if (!this.host.equals(user.username) && !this.opponent.equals(user.username)) {
            return;
        }

        this.database.insert("INSERT INTO chatline VALUES (?, ?, ?, ?)",
            (statement) -> {
                statement.setString(1, user.username);
                statement.setInt(2, this.id);
                statement.setTimestamp(3, new Timestamp(new Date().getTime()));
                statement.setString(4, message);
            }
        );
    }
}
