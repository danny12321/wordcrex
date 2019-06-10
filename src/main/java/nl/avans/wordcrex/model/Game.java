package nl.avans.wordcrex.model;

import nl.avans.wordcrex.Main;
import nl.avans.wordcrex.data.Database;
import nl.avans.wordcrex.util.ListUtil;
import nl.avans.wordcrex.util.Pair;
import nl.avans.wordcrex.util.Persistable;
import nl.avans.wordcrex.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Game implements Persistable {
    private final Database database;
    private final Wordcrex wordcrex;

    public final int id;
    public final String host;
    public final String opponent;
    public final String winner;
    public final GameState state;
    public final InviteState inviteState;
    public final Dictionary dictionary;
    public final List<Playable> pool;
    public final List<Round> rounds;
    public final List<Message> messages;

    public Game(Database database, Wordcrex wordcrex, int id, String host, String opponent, String winner, GameState state, InviteState inviteState, Dictionary dictionary, List<Playable> pool, List<Round> rounds, List<Message> messages) {
        this.database = database;
        this.wordcrex = wordcrex;
        this.id = id;
        this.host = host;
        this.opponent = opponent;
        this.winner = winner;
        this.state = state;
        this.inviteState = inviteState;
        this.dictionary = dictionary;
        this.pool = pool;
        this.rounds = rounds;
        this.messages = messages;
    }

    public static Game initialize(Database database, Wordcrex wordcrex, int id) {
        return Game.initialize(database, wordcrex, "", id).get(0);
    }

    public static List<Game> initialize(Database database, Wordcrex wordcrex, String username, GameState... states) {
        return Game.initialize(database, wordcrex, username, 0, states);
    }

    public static List<Game> initialize(Database database, Wordcrex wordcrex, String username, int id, GameState... states) {
        var ref = new Object() {
            Map<Integer, TempGame> temp = new HashMap<>();
            Map<Integer, List<Round>> rounds = new HashMap<>();
        };

        var finalStates = states.length == 0 ? GameState.values() : states;
        var where = id > 0 ? "g.game_id = ?" : "(g.username_player1 LIKE ? OR g.username_player2 LIKE ?) AND g.answer_player2 != ? AND g.game_state IN (" + StringUtil.getPlaceholders(finalStates.length) + ")";

        database.select(
            "SELECT g.game_id id, g.game_state state, g.answer_player2 invite_state, g.username_player1 host, g.username_player2 opponent, g.username_winner winner, g.letterset_code dictionary_id, group_concat(c.letter_id) ids, group_concat(c.symbol) characters, group_concat(!isnull(p.symbol)) availables FROM game g LEFT JOIN letter c ON g.game_id = c.game_id LEFT JOIN pot p ON g.game_id = p.game_id AND c.letter_id = p.letter_id WHERE " + where + " GROUP BY g.game_id",
            (statement) -> {
                if (id > 0) {
                    statement.setInt(1, id);

                    return;
                }

                statement.setString(1, username.isEmpty() ? "%" : username);
                statement.setString(2, username.isEmpty() ? "%" : username);
                statement.setString(3, InviteState.REJECTED.state);

                for (var i = 0; i < finalStates.length; i++) {
                    statement.setString(i + 4, finalStates[i].state);
                }
            },
            (result) -> {
                var game = result.getInt("id");
                var state = GameState.byState(result.getString("state"));
                var inviteState = InviteState.byState(result.getString("invite_state"));
                var host = result.getString("host");
                var opponent = result.getString("opponent");
                var winner = result.getString("winner");

                var dictionaryId = result.getString("dictionary_id");
                var dictionary = ListUtil.find(wordcrex.dictionaries, (d) -> d.id.equals(dictionaryId));

                var pool = new ArrayList<Playable>();

                if (state != GameState.PENDING) {
                    var ids = result.getString("ids").split(",");
                    var characters = result.getString("characters").split(",");
                    var availables = result.getString("availables").split(",");

                    for (var i = 0; i < ids.length; i++) {
                        var character = characters[i];

                        pool.add(new Playable(Integer.parseInt(ids[i]), Boolean.parseBoolean(availables[i]), ListUtil.find(dictionary.characters, (c) -> c.character.equals(character))));
                    }
                }

                ref.temp.put(game, new TempGame(host, opponent, winner, state, inviteState, dictionary, List.copyOf(pool)));
            }
        );

        if (ref.temp.isEmpty()) {
            return List.of();
        }

        database.select(
            "SELECT t.game_id id, t.turn_id turn, group_concat(DISTINCT b.letter_id, ' ', b.tile_x, ' ', b.tile_y) board, group_concat(DISTINCT d.letter_id) deck, h.score host_score, h.bonus host_bonus, h.turnaction_type host_action, group_concat(DISTINCT hb.letter_id, ' ', hb.tile_x, ' ', hb.tile_y) host_board, o.score opponent_score, o.bonus opponent_bonus, o.turnaction_type opponent_action, group_concat(DISTINCT ob.letter_id, ' ', ob.tile_x, ' ', ob.tile_y) opponent_board FROM turn t LEFT JOIN turnboardletter b ON t.game_id = b.game_id AND t.turn_id = b.turn_id LEFT JOIN handletter d ON t.game_id = d.game_id AND t.turn_id = d.turn_id LEFT JOIN turnplayer1 h ON t.game_id = h.game_id AND t.turn_id = h.turn_id LEFT JOIN boardplayer1 hb ON t.game_id = hb.game_id AND t.turn_id = hb.turn_id LEFT JOIN turnplayer2 o ON t.game_id = o.game_id AND t.turn_id = o.turn_id LEFT JOIN boardplayer2 ob ON t.game_id = ob.game_id AND t.turn_id = ob.turn_id WHERE t.game_id IN (" + StringUtil.getPlaceholders(ref.temp.size()) + ") GROUP BY t.game_id, t.turn_id, h.username_player1, o.username_player2",
            (statement) -> {
                var index = 0;

                for (var game : ref.temp.keySet()) {
                    statement.setInt(++index, game);
                }
            },
            (result) -> {
                var game = result.getInt("id");
                var temp = ref.temp.get(game);

                if (temp == null) {
                    return;
                }

                var turn = result.getInt("turn");
                var board = Game.parsePlayed(result.getString("board"), temp.pool, wordcrex.tiles);

                var deck = new ArrayList<Playable>();
                var deckSplitted = result.getString("deck").split(",");

                for (var s : deckSplitted) {
                    deck.add(ListUtil.find(temp.pool, (p) -> String.valueOf(p.id).equals(s)));
                }

                var host = Game.parseTurn(result, "host", temp.pool, wordcrex.tiles);
                var opponent = Game.parseTurn(result, "opponent", temp.pool, wordcrex.tiles);
                var rounds = ref.rounds.getOrDefault(game, new ArrayList<>());

                var hostScore = rounds.stream().mapToInt((r) -> r.hostTurn == null ? 0 : r.hostTurn.score + r.hostTurn.bonus).sum();
                var opponentScore = rounds.stream().mapToInt((r) -> r.opponentTurn == null ? 0 : r.opponentTurn.score + r.opponentTurn.bonus).sum();

                rounds.add(new Round(turn, board, List.copyOf(deck), hostScore, opponentScore, host, opponent));

                ref.rounds.put(game, rounds);
            }
        );

        var games = new ArrayList<Game>();

        for (var temp : ref.temp.entrySet()) {
            var game = temp.getKey();
            var rounds = ref.rounds.getOrDefault(game, new ArrayList<>());
            var data = temp.getValue();

            games.add(new Game(database, wordcrex, game, data.host, data.opponent, data.winner, data.state, data.inviteState, data.dictionary, data.pool, List.copyOf(rounds), List.of()));
        }

        for (var game : games) {
            if (!game.host.equals(username)) {
                continue;
            }

            if (game.state == GameState.PENDING && game.inviteState == InviteState.ACCEPTED) {
                game.startGame();
            } else if (game.state == GameState.PLAYING && game.getLastRound() != null && game.getLastRound().hostTurn != null && game.getLastRound().hostTurn != null) {
                game.nextRound(game.pool);
            }
        }

        return List.copyOf(games.stream()
            .sorted(Comparator.comparingInt((game) -> game.state.order))
            .collect(Collectors.toList()));
    }

    private static Turn parseTurn(ResultSet result, String player, List<Playable> pool, List<Tile> tiles) throws SQLException {
        var actionRaw = result.getString(player + "_action");

        if (actionRaw == null) {
            return null;
        }

        var action = TurnAction.byAction(actionRaw);
        var score = result.getInt(player + "_score");
        var bonus = result.getInt(player + "_bonus");
        var board = Game.parsePlayed(result.getString(player + "_board"), pool, tiles);

        return new Turn(action, score, bonus, board);
    }

    private static List<Played> parsePlayed(String played, List<Playable> pool, List<Tile> tiles) {
        if (played == null) {
            return null;
        }

        var list = new ArrayList<Played>();
        var splitted = played.split(",");

        for (var play : splitted) {
            var s = play.split(" ");

            var playableId = Integer.parseInt(s[0]);
            var playable = ListUtil.find(pool, (p) -> p.id == playableId);

            var x = Integer.parseInt(s[1]);
            var y = Integer.parseInt(s[2]);
            var tile = ListUtil.find(tiles, (t) -> t.x == x && t.y == y);

            list.add(new Played(playable, tile));
        }

        return List.copyOf(list);
    }

    private static class TempGame {
        public final String host;
        public final String opponent;
        public final String winner;
        public final GameState state;
        public final InviteState inviteState;
        public final Dictionary dictionary;
        public final List<Playable> pool;

        private TempGame(String host, String opponent, String winner, GameState state, InviteState inviteState, Dictionary dictionary, List<Playable> pool) {
            this.host = host;
            this.opponent = opponent;
            this.winner = winner;
            this.state = state;
            this.inviteState = inviteState;
            this.dictionary = dictionary;
            this.pool = pool;
        }
    }

    @Override
    public Wordcrex persist(Wordcrex model) {
        if (model.user == null) {
            return model;
        }

        var user = model.user.poll(null);
        var games = user.games.stream()
            .map((g) -> g.id == this.id ? this : g)
            .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        var observable = user.observable.stream()
            .map((g) -> g.id == this.id ? this : g)
            .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        var next = new User(this.database, this.wordcrex, user.username, user.roles, user.words, games, observable, user.manageable, user.approvable);

        return new Wordcrex(this.database, next, model.tiles, model.dictionaries);
    }

    public Game poll() {
        var game = Game.initialize(this.database, this.wordcrex, this.id);
        var messages = new ArrayList<Message>();

        this.database.select(
            "SELECT m.message, m.username, m.moment date FROM chatline m WHERE game_id = ? ORDER BY moment",
            (statement) -> statement.setInt(1, this.id),
            (result) -> messages.add(new Message(result.getString("message"), result.getString("username"), result.getDate("date")))
        );

        return new Game(this.database, this.wordcrex, this.id, game.host, game.opponent, game.winner, game.state, game.inviteState, game.dictionary, game.pool, game.rounds, List.copyOf(messages));
    }

    public Round getLastRound() {
        if (this.rounds.isEmpty()) {
            return null;
        }

        return this.rounds.get(this.rounds.size() - 1);
    }

    public void sendMessage(String username, String message) {
        if (!this.host.equals(username) && !this.opponent.equals(username)) {
            return;
        }

        this.database.insert("INSERT INTO chatline VALUES (?, ?, ?, ?)",
            (statement) -> {
                statement.setString(1, username);
                statement.setInt(2, this.id);
                statement.setTimestamp(3, new Timestamp(new Date().getTime()));
                statement.setString(4, message.trim().replaceAll("( )+", " "));
            }
        );
    }

    public int getScore(List<Played> played) {
        if (played.isEmpty()) {
            return 0;
        }

        var board = this.getLastRound().board;
        var horizontal = this.checkDirection(played, board, Pair::new);
        var vertical = this.checkDirection(played, board, (x, y) -> new Pair<>(y, x));

        if (horizontal == null || vertical == null) {
            return 0;
        }

        var score = horizontal.b + vertical.b;
        var words = new ArrayList<String>();

        words.addAll(horizontal.a);
        words.addAll(vertical.a);

        for (var word : words) {
            if (!this.dictionary.isWord(word)) {
                return 0;
            }
        }

        return score;
    }

    private Pair<List<String>, Integer> checkDirection(List<Played> played, List<Played> board, BiFunction<Integer, Integer, Pair<Integer, Integer>> coords) {
        var size = Math.sqrt(this.wordcrex.tiles.size());
        var score = 0;
        var words = new ArrayList<String>();
        var playFound = false;

        for (var y = 1; y <= size; y++) {
            var hasPlay = false;
            var hasCurrent = false;
            var tempScore = 0;
            var multipliers = new ArrayList<Integer>();
            var builder = new StringBuilder();
            var surrounded = false;

            for (var x = 1; x <= size; x++) {
                var pair = coords.apply(x, y);

                var current = this.getPlayed(pair.a, pair.b, board);
                var play = this.getPlayed(pair.a, pair.b, played);
                var tile = current == null ? play == null ? null : play.tile : current.tile;
                var letterMultiplier = 1;

                if (tile == null) {
                    if (hasPlay && playFound) {
                        return null;
                    }

                    if (hasPlay && (builder.length() > 1 || !surrounded)) {
                        playFound = true;
                    }

                    if (hasPlay && hasCurrent && builder.length() > 1) {
                        words.add(builder.toString());

                        for (var multiplier : multipliers) {
                            tempScore *= multiplier;
                        }

                        score += tempScore;
                    }

                    hasPlay = false;
                    hasCurrent = false;
                    builder.setLength(0);
                    tempScore = 0;
                    multipliers.clear();
                    surrounded = false;

                    continue;
                }

                if (tile.type == TileType.LETTER) {
                    letterMultiplier = tile.multiplier;
                } else if (tile.type == TileType.WORD) {
                    multipliers.add(tile.multiplier);
                }

                if (current != null) {
                    hasCurrent = true;
                    builder.append(current.playable.character.character);
                    tempScore += current.playable.character.value;
                } else if (play != null) {
                    hasPlay = true;
                    builder.append(play.playable.character.character);
                    tempScore += (play.playable.character.value * letterMultiplier);

                    if (tile.type == TileType.CENTER) {
                        hasCurrent = true;
                        multipliers.add(tile.multiplier);
                    }

                    for (var side : TileSide.values()) {
                        if (this.getPlayed(pair.a + side.x, pair.b + side.y, board) != null) {
                            hasCurrent = true;
                        }

                        if (this.getPlayed(pair.a + side.x, pair.b + side.y, played) != null) {
                            surrounded = true;
                        }
                    }
                }
            }
        }

        if (played.size() == 7) {
            score += 100;
        }

        return new Pair<>(words, score);
    }

    private Played getPlayed(int x, int y, List<Played> played) {
        return ListUtil.find(played, (p) -> p.tile.x == x && p.tile.y == y);
    }

    public void startGame() {
        this.database.update(
            "UPDATE game g SET g.game_state = ? WHERE g.game_id = ?",
            (statement) -> {
                statement.setString(1, GameState.PLAYING.state);
                statement.setInt(2, this.id);
            }
        );

        var playable = new ArrayList<Playable>();
        var id = 0;

        for (var character : this.dictionary.characters) {
            for (var i = 0; i < character.amount; i++) {
                playable.add(new Playable(++id, true, character));
            }
        }

        this.database.insert(
            "INSERT INTO letter VALUES " + playable.stream().map((p) -> "(?, ?, ?, ?)").collect(Collectors.joining(", ")),
            (statement) -> {
                var index = 0;

                for (var p : playable) {
                    statement.setInt(++index, p.id);
                    statement.setInt(++index, this.id);
                    statement.setString(++index, this.dictionary.id);
                    statement.setString(++index, p.character.character);
                }
            }
        );

        this.nextRound(playable);
    }

    private void nextRound(List<Playable> pool) {
        if (!this.rounds.isEmpty() && (this.getLastRound().hostTurn == null || this.getLastRound().opponentTurn == null)) {
            return;
        }

        var turn = this.rounds.size() + 1;
        var deck = new ArrayList<Playable>();
        var round = this.getLastRound();

        if (round != null) {
            deck.addAll(round.deck.stream()
                .filter((a) -> round.board.stream().noneMatch((p) -> p.playable.id == a.id))
                .collect(Collectors.toList()));
        }

        var add = Math.min(7 - deck.size(), pool.size());

        for (var i = 0; i < add; i++) {
            Playable playable;

            do {
                playable = pool.get(Main.RANDOM.nextInt(pool.size()));
            } while (!playable.available || this.hasPlayable(deck, playable));

            deck.add(playable);
        }

        this.database.insert(
            "INSERT INTO turn VALUES (?, ?)",
            (statement) -> {
                statement.setInt(1, this.id);
                statement.setInt(2, turn);
            }
        );

        this.database.insert(
            "INSERT INTO handletter VALUES " + deck.stream().map((p) -> "(?, ?, ?)").collect(Collectors.joining(", ")),
            (statement) -> {
                var index = 0;

                for (var playable : deck) {
                    statement.setInt(++index, this.id);
                    statement.setInt(++index, turn);
                    statement.setInt(++index, playable.id);
                }
            }
        );
    }

    private boolean hasPlayable(List<Playable> playable, Playable p) {
        return playable.stream().anyMatch((d) -> d.id == p.id);
    }

    public void playTurn(String username, List<Played> played) {
        throw new RuntimeException();
    }

    public void resign(String username) {
        throw new RuntimeException();
    }
}
