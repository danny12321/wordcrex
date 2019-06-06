package nl.avans.wordcrex.model;

import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.Pollable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Game implements Pollable<Game> {
    private final Database database;

    public final int id;
    public final String host;
    public final String opponent;
    public final String winner;
    public final GameState state;
    public final InviteState inviteState;
    public final Dictionary dictionary;
    public final List<Tile> tiles;
    public final Map<Letter, Boolean> pool;
    public final List<Round> rounds;
    public final List<Message> messages;

    public Game(Game game, List<Tile> tiles) {
        this(game.database, game.id, game.host, game.opponent, game.winner, game.state, game.inviteState, game.dictionary, tiles, game.pool, game.rounds, game.messages);
    }

    public Game(Game game, String winner, GameState state, InviteState inviteState, List<Message> messages) {
        this(game.database, game.id, game.host, game.opponent, winner, state, inviteState, game.dictionary, game.tiles, null, null, messages);
    }

    public Game(Database database, int id, String host, String opponent, String winner, GameState state, InviteState inviteState, Dictionary dictionary) {
        this(database, id, host, opponent, winner, state, inviteState, dictionary, List.of(), null, null, List.of());
    }

    public Game(Database database, int id, String host, String opponent, String winner, GameState state, InviteState inviteState, Dictionary dictionary, List<Tile> tiles, Map<Letter, Boolean> pool, List<Round> rounds, List<Message> messages) {
        this.database = database;
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.winner = winner;
        this.state = state;
        this.inviteState = inviteState;
        this.dictionary = dictionary;
        this.tiles = tiles;
        this.pool = pool == null ? this.getPool() : pool;
        this.rounds = rounds == null ? this.getRounds() : rounds;
        this.messages = messages;
    }

    @Override
    public Game initialize() {
        var tiles = new ArrayList<Tile>();

        this.database.select(
            "SELECT t.x, t.y, t.tile_type FROM tile t ORDER BY t.x, t.y",
            (statement) -> {
            },
            (result) -> tiles.add(new Tile(result.getInt("x"), result.getInt("y"), result.getString("tile_type")))
        );

        return new Game(this.poll(), List.copyOf(tiles));
    }

    @Override
    public Game poll() {
        var ref = new Object() {
            String winner;
            GameState state;
            InviteState inviteState;
        };
        this.database.select(
            "SELECT g.game_state state, g.answer_player2 invite_state, g.username_winner winner FROM game g WHERE g.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                ref.winner = result.getString("winner");
                ref.state = GameState.byState(result.getString("state"));
                ref.inviteState = InviteState.byState(result.getString("invite_state"));
            }
        );

        var messages = new ArrayList<Message>();

        this.database.select(
            "SELECT m.message, m.username, m.moment date FROM chatline m WHERE game_id = ? ORDER BY moment",
            (statement) -> statement.setInt(1, this.id),
            (result) -> messages.add(new Message(result.getString("message"), result.getString("username"), result.getDate("date")))
        );

        return new Game(this, ref.winner, ref.state, ref.inviteState, List.copyOf(messages));
    }

    private Map<Letter, Boolean> getPool() {
        var pool = new HashMap<Letter, Boolean>();

        this.database.select(
            "SELECT l.letter_id id, l.symbol `character`, !isnull(p.symbol) available FROM letter l LEFT JOIN pot p ON l.game_id = p.game_id AND l.letter_id = p.letter_id WHERE l.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                var id = result.getInt("id");
                var symbol = result.getString("character");
                var character = this.dictionary.characters.stream()
                    .filter((c) -> c.character.equals(symbol))
                    .findFirst()
                    .orElseThrow();
                var available = result.getBoolean("available");

                pool.put(new Letter(id, character), available);
            }
        );

        return Map.copyOf(pool);
    }

    private List<Round> getRounds() {
        var rounds = new ArrayList<Round>();
        var played = new ArrayList<Played>();

        this.database.select(
            "SELECT t.turn_id                       turn, " +
                "       (SELECT group_concat(l.letter_id SEPARATOR ',') " +
                "        FROM handletter l " +
                "        WHERE t.game_id = l.game_id " +
                "          AND t.turn_id = l.turn_id " +
                "        GROUP BY l.game_id, l.turn_id) deck, " +
                "       h.turnaction_type               host_action, " +
                "       h.score                         host_score, " +
                "       h.bonus                         host_bonus, " +
                "       hp.woorddeel                    host_played, " +
                "       hp.`x-waarden`                  host_x, " +
                "       hp.`y-waarden`                  host_y, " +
                "       o.turnaction_type               opponent_action, " +
                "       o.score                         opponent_score, " +
                "       o.bonus                         opponent_bonus, " +
                "       op.woorddeel                    opponent_played, " +
                "       op.`x-waarden`                  opponent_x, " +
                "       op.`y-waarden`                  opponent_y " +
                "FROM turn t " +
                "         LEFT JOIN turnplayer1 h ON t.game_id = h.game_id AND t.turn_id = h.turn_id " +
                "         LEFT JOIN gelegdplayer1 hp ON t.game_id = hp.game_id AND t.turn_id = hp.turn_id " +
                "         LEFT JOIN turnplayer2 o ON t.game_id = o.game_id AND t.turn_id = o.turn_id " +
                "         LEFT JOIN gelegdplayer2 op ON t.game_id = op.game_id AND t.turn_id = op.turn_id " +
                "         LEFT JOIN hand d ON t.game_id = d.game_id AND t.turn_id = d.turn_id " +
                "WHERE t.game_id = ?",
            (statement) -> statement.setInt(1, this.id),
            (result) -> {
                var deck = new ArrayList<Letter>();
                var deckRaw = result.getString("deck");

                if (deckRaw == null) {
                    return;
                }

                var deckSplitted = deckRaw.split(",");

                for (var id : deckSplitted) {
                    deck.add(this.pool.keySet().stream()
                        .filter((c) -> String.valueOf(c.id).equals(id))
                        .findFirst()
                        .orElseThrow());
                }

                var hostTurn = this.parseTurn(result, "host", deck);
                var opponentTurn = this.parseTurn(result, "opponent", deck);

                if (hostTurn != null && opponentTurn != null) {
                    played.addAll(hostTurn.score + hostTurn.bonus > opponentTurn.score + opponentTurn.bonus ? hostTurn.played : opponentTurn.played);
                }

                var hostScore = rounds.stream()
                    .mapToInt((round) -> {
                        if (round.hostTurn == null) {
                            return 0;
                        }

                        return round.hostTurn.score + round.hostTurn.bonus;
                    })
                    .sum();

                var opponentScore = rounds.stream()
                    .mapToInt((round) -> {
                        if (round.opponentTurn == null) {
                            return 0;
                        }

                        return round.opponentTurn.score + round.opponentTurn.bonus;
                    })
                    .sum();

                rounds.add(new Round(result.getInt("turn"), List.copyOf(deck), hostTurn, opponentTurn, hostScore, opponentScore, List.copyOf(played)));
            }
        );

        return List.copyOf(rounds);
    }

    private Turn parseTurn(ResultSet result, String player, List<Letter> deck) throws SQLException {
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

                var character = deck.stream()
                    .filter((c) -> c.character.character.equals(rawSplitted[index]))
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

        var pool = new ArrayList<Letter>();

        this.getPool().forEach((k, v) -> {
            if (v) {
                pool.add(k);
            }
        });

        var values = new ArrayList<String>();
        var deck = new ArrayList<Letter>();
        var size = Math.min(7, pool.size() - 1);
        var random = new Random();

        if (!this.rounds.isEmpty()) {
            var last = this.getLastRound();

            if (last.hostTurn != null && last.opponentTurn != null) {
                var highest = last.hostTurn.score + last.hostTurn.bonus > last.opponentTurn.score + last.opponentTurn.bonus ? last.hostTurn : last.opponentTurn;

                for (var r : last.deck) {
                    var found = false;

                    for (var p : highest.played) {
                        if (p.letter.id == r.id) {
                            found = true;

                            break;
                        }
                    }

                    if (!found) {
                        deck.add(r);
                    }
                }
            }
        }

        var add = size - deck.size();
        var added = new ArrayList<Integer>();

        for (int i = 0; i < add; i++) {
            var j = random.nextInt(pool.size());

            while (added.indexOf(j) != -1) {
                j = random.nextInt(pool.size());
            }

            added.add(j);
            deck.add(pool.get(j));
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
                statement.setString(4, message.trim().replaceAll("( )+", " "));
            }
        );
    }

    public int getScore(List<Played> played) {
        if (played.isEmpty()) {
            return -1;
        }

        var board = this.getLastRound().board;
        var horizontal = this.checkDirection(played, board, Pair::new);
        var vertical = this.checkDirection(played, board, (x, y) -> new Pair<>(y, x));

        if (horizontal == null || vertical == null) {
            return -1;
        }

        var score = horizontal.b + vertical.b;
        var words = new ArrayList<String>();

        words.addAll(horizontal.a);
        words.addAll(vertical.a);

        for (var word : words) {
            if (!this.dictionary.isWord(word)) {
                return -1;
            }
        }

        return score;
    }

    private Pair<List<String>, Integer> checkDirection(List<Played> played, List<Played> board, BiFunction<Integer, Integer, Pair<Integer, Integer>> coords) {
        var extra = 0;
        var size = Math.sqrt(this.tiles.size());
        var center = (int) Math.ceil(size / 2);
        var score = 0;
        var words = new ArrayList<String>();
        var playFound = false;

        for (var y = 1; y <= size; y++) {
            var hasPlay = false;
            var hasCurrent = false;
            var tempScore = 0;
            var multipliers = new ArrayList<Integer>();
            var builder = new StringBuilder();
            var playCount = 0;
            var surrounded = false;
            var order = new ArrayList<Boolean>();

            for (var x = 1; x <= size; x++) {
                var pair = coords.apply(x, y);
                var tile = this.getTile(pair.a, pair.b);

                if (tile == null) {
                    throw new RuntimeException();
                }

                var current = this.getPlayed(pair.a, pair.b, board);
                var play = this.getPlayed(pair.a, pair.b, played);
                var letterMultiplier = 1;

                switch (tile.type) {
                    case "2L":
                        letterMultiplier = 2;
                        break;
                    case "4L":
                        letterMultiplier = 4;
                        break;
                    case "6L":
                        letterMultiplier = 6;
                        break;
                    case "3W":
                        multipliers.add(3);
                        break;
                    case "4W":
                        multipliers.add(4);
                        break;
                }

                if (current != null) {
                    hasCurrent = true;
                    builder.append(current.letter.character.character);
                    tempScore += current.letter.character.value;
                    order.add(false);
                } else if (play != null) {
                    hasPlay = true;
                    builder.append(play.letter.character.character);
                    tempScore += (play.letter.character.value * letterMultiplier);
                    playCount++;
                    order.add(true);

                    if (x == center && y == center) {
                        hasCurrent = true;
                        multipliers.add(2);
                    }

                    for (var side : TileSide.values()) {
                        if (this.getPlayed(pair.a + side.x, pair.b + side.y, board) != null) {
                            hasCurrent = true;
                        }
                        if (this.getPlayed(pair.a + side.x, pair.b + side.y, played) != null) {
                            surrounded = true;
                        }
                    }
                } else {
                    if (hasPlay && playFound) {
                        return null;
                    }

                    if (hasPlay && (builder.length() > 1 || !surrounded)) {
                        playFound = true;
                    }

                    if (hasPlay && hasCurrent && builder.length() > 1) {
                        Boolean last = null;
                        var found = false;

                        for (var b : order) {
                            if (b != last) {
                                if (b && found) {
                                    return null;
                                }

                                last = b;

                                if (b) {
                                    found = true;
                                }
                            }
                        }

                        words.add(builder.toString());

                        for (var multiplier : multipliers) {
                            tempScore *= multiplier;
                        }

                        score += tempScore;

                        if (playCount == 7) {
                            extra = 100;
                        }
                    }

                    hasPlay = false;
                    hasCurrent = false;
                    builder.setLength(0);
                    tempScore = 0;
                    multipliers.clear();
                    playCount = 0;
                    surrounded = false;
                    order.clear();
                }
            }
        }

        return new Pair<>(words, score + extra);
    }

    private Played getPlayed(int x, int y, List<Played> played) {
        for (var p : played) {
            if (p.x == x && p.y == y) {
                return p;
            }
        }

        return null;
    }

    private Tile getTile(int x, int y) {
        for (var tile : this.tiles) {
            if (tile.x == x && tile.y == y) {
                return tile;
            }
        }

        return null;
    }

    public void playTurn(String username, TurnAction action, List<Played> played){
        var ref = new Object() {
            String username1;
            String username2;
        };
        this.database.select(
                "SELECT username_player1, username_player2 from game WHERE game_id = ?",
                (statement) -> statement.setInt(1, this.id),
                (result) -> {
                    ref.username1 = result.getString("username_player1");
                    ref.username2 = result.getString("username_player2");
                }
        );

        if(action == TurnAction.PLAYED){
            if(username.equals(ref.username1)){
                this.database.insert(
                        "INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type) VALUES (?, ?, ?, 0, ?, ?)",
                        (statement) -> {
                            statement.setInt(1, this.id);
                            statement.setInt(2, this.rounds.size());
                            statement.setString(3, username);
                            statement.setInt(4, getScore(played));
                            statement.setString(5, TurnAction.PLAYED.action);
                        }
                );
                var values = new ArrayList<String>();
                for (int i = 0; i < played.size(); i++) {
                    values.add("(?, ?, ?, ?, ?, ?)");
                }

                this.database.insert(
                        "INSERT INTO boardplayer1 (game_id, username, turn_id, letter_id, tile_x, tile_y) VALUES " +  String.join(", ", values) ,
                        (statement) -> {
                            var offset = 0;
                            for(int i =0; i < played.size(); i++) {
                                statement.setInt(++offset, this.id);
                                statement.setString(++offset, username);
                                statement.setInt(++offset, this.rounds.size());
                                statement.setInt(++offset, played.get(i).letter.id);
                                statement.setInt(++offset, played.get(i).x);
                                statement.setInt(++offset, played.get(i).y);
                            }
                        }
                );
                if(this.getLastRound().opponentTurn!= null){
                    if(this.getScore(played) == this.getLastRound().opponentScore){
                        this.database.update("UPDATE turnplayer2 T SET T.bonus = 5 WHERE T.game_id = ? AND T.turn_id = ?",
                                (statement) -> {
                                    statement.setInt(1, this.id);
                                    statement.setInt(2, this.rounds.size());
                                }
                        );
                    }
                }

            } else if (username.equals(ref.username2)){
                this.database.insert(
                        "INSERT INTO turnplayer2 (game_id, turn_id, username_player2, bonus, score, turnaction_type) VALUES (?, ?, ?, 0, ?, ?)",
                        (statement) -> {
                            statement.setInt(1, this.id);
                            statement.setInt(2, this.rounds.size());
                            statement.setString(3, username);
                            statement.setInt(4, getScore(played));
                            statement.setString(5, TurnAction.PLAYED.action);
                        }
                );
                var values = new ArrayList<String>();
                for (int i = 0; i < played.size(); i++) {
                    values.add("(?, ?, ?, ?, ?, ?)");
                }
                this.database.insert(
                        "INSERT INTO boardplayer2 (game_id, username, turn_id, letter_id, tile_x, tile_y) VALUES " + String.join(", ", values) ,
                (statement) -> {
                    var offset = 0;
                    for(int i =0; i < played.size(); i++) {
                        statement.setInt(++offset, this.id);
                        statement.setString(++offset, username);
                        statement.setInt(++offset, this.rounds.size());
                        statement.setInt(++offset, played.get(i).letter.id);
                        statement.setInt(++offset, played.get(i).x);
                        statement.setInt(++offset, played.get(i).y);
                    }
                }
                );
                if(this.getLastRound().hostTurn != null){
                    if(this.getScore(played) == this.getLastRound().hostTurn.score){
                        this.database.update("UPDATE turnplayer1 T SET T.bonus = 5 WHERE T.game_id = ? AND T.turn_id = ?",
                                (statement) -> {
                                    statement.setInt(1, this.id);
                                    statement.setInt(2, this.rounds.size());
                                }
                        );
                    }
                }
            }
        } else if (action == TurnAction.PASSED) {
            if(username.equals(ref.username1)){
                this.database.insert(
                        "INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type) VALUES (?, ?, ?, 0, 0, ?)",
                        (statement) -> {
                            statement.setInt(1, this.id);
                            statement.setInt(2, this.rounds.size());
                            statement.setString(3, username);
                            statement.setString(4, TurnAction.PASSED.action);
                        }
                );

            } else if (username.equals(ref.username2)){
                this.database.insert(
                        "INSERT INTO turnplayer2 (game_id, turn_id, username_player2, bonus, score, turnaction_type) VALUES (?, ?, ?, 0, 0, ?)",
                        (statement) -> {
                            statement.setInt(1, this.id);
                            statement.setInt(2, this.rounds.size());
                            statement.setString(3, username);
                            statement.setString(4, TurnAction.PASSED.action);
                        }
                );
            }
        }
        if(this.getLastRound().hostTurn != null){
            if(this.getLastRound().hostTurn.action == TurnAction.RESIGNED){
                var resigned = TurnAction.RESIGNED.action;
                this.database.update(
                    "UPDATE game g SET g.game_state = ?, g.username_winner = ? where game_id = ?",
                    (statement) -> {
                        statement.setString(1, resigned);
                        statement.setString(2, this.opponent);
                        statement.setInt(3, this.id);
                    }
                );
                return;
            }

        } else if(this.getLastRound().opponentTurn != null){
            if(this.getLastRound().opponentTurn.action == TurnAction.RESIGNED){
                var resigned = TurnAction.RESIGNED.action;
                this.database.update(
                        "UPDATE game g SET g.game_state = ?, g.username_winner = ? where game_id = ?",
                        (statement) -> {
                            statement.setString(1, resigned);
                            statement.setString(2, this.host);
                            statement.setInt(3, this.id);
                        }
                );
                return;
            }
        } if(this.getLastRound().opponentTurn!= null || this.getLastRound().hostTurn!= null){
            startNewRound();
        }
    }

    public void resign(String username){
        var ref = new Object() {
            String username1;
            String username2;
        };
        this.database.select(
                "SELECT username_player1, username_player2 from game WHERE game_id = ?",
                (statement) -> statement.setInt(1, this.id),
                (result) -> {
                    ref.username1 = result.getString("username_player1");
                    ref.username2 = result.getString("username_player2");
                }
        );
        if(username.equals(ref.username1)){
            this.database.insert(
                    "INSERT INTO turnplayer1 (game_id, turn_id, username_player1, bonus, score, turnaction_type) VALUES (?, ?, ?, 0, 0, ?)",
                    (statement) -> {
                        statement.setInt(1, this.id);
                        statement.setInt(2, this.rounds.size());
                        statement.setString(3, username);
                        statement.setString(4, TurnAction.RESIGNED.action);
                    }
            );
            if(this.getLastRound().opponentTurn.action == TurnAction.RESIGNED){
                this.database.update("UPDATE game SET game_state = ?, username_winner = ? WHERE game_id = ?",
                        (statement) -> {
                            statement.setString(1, GameState.FINISHED.state);
                            statement.setString(2, this.host);
                            statement.setInt(3, this.id);
                        });
            }
        } else if (username.equals(ref.username2)){
            this.database.insert(
                    "INSERT INTO turnplayer2 (game_id, turn_id, username_player2, bonus, score, turnaction_type) VALUES (?, ?, ?, 0, 0, ?)",
                    (statement) -> {
                        statement.setInt(1, this.id);
                        statement.setInt(2, this.rounds.size());
                        statement.setString(3, username);
                        statement.setString(4, TurnAction.RESIGNED.action);
                    }
            );
            if(this.getLastRound().hostTurn != null) {
                if (this.getLastRound().hostTurn.action == TurnAction.RESIGNED) {
                    this.database.update("UPDATE game SET game_state = ?, username_winner = ? WHERE game_id = ?",
                            (statement) -> {
                                statement.setString(1, GameState.FINISHED.state);
                                statement.setString(2, this.host);
                                statement.setInt(3, this.id);
                            });
                }
            }
        }

    }
}
