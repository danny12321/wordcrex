package nl.avans.wordcrex;

import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.model.Character;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {
    private List<Tile> createBoard() {
        var tiles = new ArrayList<Tile>();

        for (var x = 1; x <= 15; x++) {
            for (var y = 1; y <= 15; y++) {
                var type = TileType.NONE;
                var multiplier = 1;

                if (y == 1) {
                    type = TileType.LETTER;
                    multiplier = (x % 3) * 2;
                } else if (y == 5) {
                    type = TileType.WORD;
                    multiplier = x % 2 + 2;
                } else if (x == 8 && y == 8) {
                    type = TileType.CENTER;
                }

                tiles.add(new Tile(x, y, type, multiplier));
            }
        }

        return List.copyOf(tiles);
    }

    private int getScore(BiFunction<List<Playable>, List<Tile>, List<Played>> board, BiFunction<List<Playable>, List<Tile>, List<Played>> played) {
        var character = new Character("A", 1, 1);
        var playables = new ArrayList<Playable>();

        for (var i = 0; i < 25; i++) {
            playables.add(new Playable(i, true, character));
        }

        var wordcrex = new Wordcrex(null, null, this.createBoard(), List.of());
        var game = new Game(null, wordcrex, 1, "", "", null, GameState.PLAYING, InviteState.ACCEPTED, null, List.copyOf(playables), List.of(), List.of());

        return game.getScore(board.apply(playables, wordcrex.tiles), played.apply(playables, wordcrex.tiles), false);
    }

    private Tile findTile(int x, int y, List<Tile> tiles) {
        for (var tile : tiles) {
            if (tile.x == x && tile.y == y) {
                return tile;
            }
        }

        return null;
    }

    @Test
    @DisplayName("Word length must be greater than 1")
    public void testNewWordLength() {
        // No characters played
        var score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of());

        assertEquals(0, score);

        // Only 1 character played in the center
        score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles))
        ));

        assertEquals(0, score);
    }

    @Test
    @DisplayName("Word must be connected to the center")
    public void testIsInMiddle() {
        // Connected to the center
        var score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles))
        ));

        assertEquals(2, score);

        // Not connected to the center
        score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(9, 8, tiles)),
            new Played(playables.get(1), this.findTile(10, 8, tiles))
        ));

        assertEquals(0, score);
    }

    @Test
    @DisplayName("Word is adjacent to existing word")
    public void testAdjacent() {
        // Add horizontally
        var score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles))
        ), (playables, tiles) -> List.of(
            new Played(playables.get(2), this.findTile(10, 8, tiles)),
            new Played(playables.get(3), this.findTile(11, 8, tiles))
        ));

        assertEquals(4, score);

        // Add vertically
        score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles))
        ), (playables, tiles) -> List.of(
            new Played(playables.get(2), this.findTile(8, 9, tiles)),
            new Played(playables.get(3), this.findTile(8, 10, tiles))
        ));

        assertEquals(3, score);

        // Add vertically in front
        score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles))
        ), (playables, tiles) -> List.of(
            new Played(playables.get(2), this.findTile(7, 8, tiles)),
            new Played(playables.get(3), this.findTile(7, 9, tiles)),
            new Played(playables.get(4), this.findTile(7, 10, tiles))
        ));

        assertEquals(6, score);

        // Add word non-adjacent
        score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles))
        ), (playables, tiles) -> List.of(
            new Played(playables.get(2), this.findTile(10, 10, tiles)),
            new Played(playables.get(3), this.findTile(11, 10, tiles))
        ));

        assertEquals(0, score);
    }

    @Test
    @DisplayName("New word must be on 1 consistent axis")
    public void testAxis() {
        // Word axis are both not consistent
        var score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles)),
            new Played(playables.get(2), this.findTile(10, 9, tiles))
        ));

        assertEquals(0, score);

        // 1 word axis is consistent
        score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles)),
            new Played(playables.get(2), this.findTile(10, 8, tiles))
        ));

        assertEquals(3, score);
    }

    @Test
    @DisplayName("New word does not contain empty tiles")
    public void testTiles() {
        // Word contains an empty tile
        var score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles)),
            new Played(playables.get(2), this.findTile(11, 8, tiles))
        ));

        assertEquals(0, score);

        // Row contains an empty tile
        score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles)),
            new Played(playables.get(2), this.findTile(10, 8, tiles))
            ), (playables, tiles) -> List.of(
            new Played(playables.get(3), this.findTile(6, 8, tiles)),
            new Played(playables.get(4), this.findTile(7, 8, tiles)),
            new Played(playables.get(5), this.findTile(12, 8, tiles))
        ));

        assertEquals(0, score);
    }

    @Test
    @DisplayName("Score adjusts according to multipliers")
    public void testMultipliers() {
        // Word is on no multipliers
        var score = this.getScore((playables, tiles) -> List.of(), (playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(8, 8, tiles)),
            new Played(playables.get(1), this.findTile(9, 8, tiles)),
            new Played(playables.get(2), this.findTile(10, 8, tiles))
        ));

        assertEquals(3, score);

        // Test character multiplier (2L)
        score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(6, 8, tiles)),
            new Played(playables.get(1), this.findTile(7, 8, tiles)),
            new Played(playables.get(2), this.findTile(8, 8, tiles)),
            new Played(playables.get(3), this.findTile(8, 7, tiles)),
            new Played(playables.get(4), this.findTile(8, 6, tiles)),
            new Played(playables.get(5), this.findTile(8, 5, tiles)),
            new Played(playables.get(6), this.findTile(8, 4, tiles)),
            new Played(playables.get(7), this.findTile(8, 3, tiles)),
            new Played(playables.get(8), this.findTile(8, 2, tiles)),
            new Played(playables.get(9), this.findTile(8, 1, tiles))

        ), (playables, tiles) -> List.of(
            new Played(playables.get(10), this.findTile(7, 1, tiles))
        ));

        assertEquals(3, score);

        // Test word multiplier (3W)
        score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(6, 8, tiles)),
            new Played(playables.get(1), this.findTile(7, 8, tiles)),
            new Played(playables.get(2), this.findTile(8, 8, tiles)),
            new Played(playables.get(3), this.findTile(8, 7, tiles)),
            new Played(playables.get(4), this.findTile(8, 6, tiles)),
            new Played(playables.get(5), this.findTile(8, 5, tiles))

        ), (playables, tiles) -> List.of(
            new Played(playables.get(6), this.findTile(7, 5, tiles))
        ));

        assertEquals(6, score);

        // Test multiplier order (Character (2L) -> Word (3W))
        score = this.getScore((playables, tiles) -> List.of(
            new Played(playables.get(0), this.findTile(6, 8, tiles)),
            new Played(playables.get(1), this.findTile(7, 8, tiles)),
            new Played(playables.get(2), this.findTile(8, 8, tiles)),
            new Played(playables.get(3), this.findTile(8, 7, tiles)),
            new Played(playables.get(4), this.findTile(8, 6, tiles)),
            new Played(playables.get(5), this.findTile(8, 5, tiles))

        ), (playables, tiles) -> List.of(
            new Played(playables.get(6), this.findTile(7, 1, tiles)),
            new Played(playables.get(7), this.findTile(7, 2, tiles)),
            new Played(playables.get(8), this.findTile(7, 3, tiles)),
            new Played(playables.get(9), this.findTile(7, 4, tiles)),
            new Played(playables.get(10), this.findTile(7, 5, tiles))
        ));

        assertEquals(24, score);
    }
}
