package nl.avans.wordcrex.model;

import nl.avans.wordcrex.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Round {
    public final int round;
    public final List<Character> characters;
    public final Turn hostTurn;
    public final Turn opponentTurn;
    public final List<Played> board;

    public Round(int round, List<Character> characters, Turn hostTurn, Turn opponentTurn, List<Played> board) {
        this.round = round;
        this.characters = characters;
        this.hostTurn = hostTurn;
        this.opponentTurn = opponentTurn;
        this.board = board;
    }

    public int getScore(List<Tile> tiles, List<Played> played, Dictionary dictionary) {
        if (played.isEmpty()) {
            return -1;
        }

        var multiplier = 0;
        var score = 0;
        TileSide side = null;
        var sorted = played.stream()
            .sorted(Comparator.comparingInt((a) -> a.x + a.y))
            .collect(Collectors.toList());
        var check = new ArrayList<Played>();

        for (var play : sorted) {
            var found = false;

            check.add(play);

            for (var s : TileSide.values()) {
                if (side != null && s == side.invert()) {
                    continue;
                }

                if (s != side) {
                    var current = this.go(play.x + s.x, play.y + s.y, s, new ArrayList<>());

                    for (var c : current) {
                        score += c.character.value;
                    }

                    check.addAll(current);
                }

                var other = this.find(sorted, play.x + s.x, play.y + s.y, (o, p) -> o.x == p.a && o.y == p.b);

                if (other == null) {
                    continue;
                }

                if (side == null) {
                    side = s;
                } else if (s != side) {
                    return -1;
                }

                found = true;
            }

            var tile = this.find(tiles, play.x, play.y, (t, p) -> t.x == p.a && t.y == p.b);
            var add = play.character.value;

            if (tile == null) {
                throw new RuntimeException("what did you do");
            }

            switch (tile.type) {
                case "2L":
                    add *= 2;
                    break;
                case "4L":
                    add *= 4;
                    break;
                case "6L":
                    add *= 6;
                    break;
                case "3W":
                    multiplier += 3;
                    break;
                case "4W":
                    multiplier += 4;
                    break;
            }

            score += add;

            if (!found) {
                break;
            }
        }

        /*if (!this.checkDirection(tiles, check, dictionary, Pair::new) || !this.checkDirection(tiles, check, dictionary, (x, y) -> new Pair<>(y, x))) {
            return -1;
        }*/

        if (multiplier > 0) {
            score *= multiplier;
        }

        return score;
    }

    private <T> T find(List<T> list, int x, int y, BiFunction<T, Pair<Integer, Integer>, Boolean> is) {
        for (var p : list) {
            if (is.apply(p, new Pair<>(x, y))) {
                return p;
            }
        }

        return null;
    }

    private List<Played> go(int x, int y, TileSide side, List<Played> previous) {
        var played = this.find(this.board, x, y, (o, p) -> o.x == p.a && o.y == p.b);

        if (played == null) {
            return previous;
        }

        previous.add(played);

        return this.go(x + side.x, y + side.y, side, previous);
    }

    private boolean checkDirection(List<Tile> tiles, List<Played> check, Dictionary dictionary, BiFunction<Integer, Integer, Pair<Integer, Integer>> coords) {
        var size = Math.sqrt(tiles.size());

        for (var x = 0; x < size; x++) {
            var word = new StringBuilder();

            for (var y = 0; y < size; y++) {
                var pair = coords.apply(x, y);
                var play = this.find(check, pair.a, pair.b, (t, p) -> t.x == p.a && t.y == p.b);

                if (play == null) {
                    continue;
                }

                word.append(play.character.character);
            }

            System.out.println("found word " + word);

            if (!dictionary.isWord(word.toString())) {
                return false;
            }
        }

        return true;
    }
}
