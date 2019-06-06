package nl.avans.wordcrex;

import nl.avans.wordcrex.model.*;
import nl.avans.wordcrex.model.Character;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JUnit5ExampleTest {
    Character character;
    Letter letter;
    Played played;
    List<Letter> deck;
    List<Played> currentBoard;
    Round round;
    Game game;


    @DisplayName("No tiles found")
    @Test
    void noTiles() {
        character = new Character("a", 2, 1);
        letter = new Letter(1, character);
        played = new Played(letter, 8, 8);
        deck = List.of(letter);
        currentBoard = List.of();
        round = new Round(1, deck, null, null, 0, 0, currentBoard);

        game = new Game(null, 1, "HostName", "OpponentName", null, GameState.PLAYING, InviteState.ACCEPTED, null, List.of(), Map.of(), List.of(round), List.of());
        assertEquals(-1, game.getScore(List.of(played)));
    }

    @DisplayName("No tiles found in the middle")
    @Test
    void notMiddle() {
        character = new Character("a", 2, 1);
        letter = new Letter(1, character);
        played = new Played(letter, 7, 7);
        deck = List.of();
        currentBoard = List.of();
        round = new Round(1, deck, null, null, 0, 0, currentBoard);

        game = new Game(null, 1, "HostName", "OpponentName", null, GameState.PLAYING, InviteState.ACCEPTED, null, List.of(), Map.of(), List.of(round), List.of());
        assertEquals(-1, game.getScore(List.of(played)));
    }

    @DisplayName("Word found with tile in the middle")
    @Test
    void inMiddle() {
        var characterA = new Character("a", 2, 1);
        var characterL = new Character("l", 2, 1);
        var letter1 = new Letter(1, characterA);
        var letter2 = new Letter(2, characterL);
        var played1 = new Played(letter1, 8, 8);
        var played2 = new Played(letter2, 9, 8);
        deck = List.of();
        currentBoard = List.of();
        round = new Round(1, deck, null, null, 0, 0, currentBoard);

        game = new Game(null, 1, "HostName", "OpponentName", null, GameState.PLAYING, InviteState.ACCEPTED, null, List.of(), Map.of(), List.of(round), List.of());
        assertEquals(-1, game.getScore(List.of(played1, played2)));
    }


}
