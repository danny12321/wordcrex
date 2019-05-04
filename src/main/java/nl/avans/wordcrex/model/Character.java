package nl.avans.wordcrex.model;

import java.util.Random;

public enum Character {
    A('a', 2, 7),
    B('b', 3, 2),
    C('c', 6, 2),
    D('d', 2, 5),
    E('e', 1, 10),
    F('f', 6, 2),
    G('g', 4, 3),
    H('h', 5, 2),
    I('i', 2, 4),
    J('j', 5, 2),
    K('k', 4, 3),
    L('l', 4, 3),
    M('m', 3, 3),
    N('n', 1, 11),
    O('o', 2, 6),
    P('p', 5, 2),
    Q('q', 20, 1),
    R('r', 2, 5),
    S('s', 3, 5),
    T('t', 2, 5),
    U('u', 3, 3),
    V('v', 5, 2),
    W('w', 6, 2),
    X('x', 8, 1),
    Y('y', 9, 1),
    Z('z', 6, 2);

    public final char character;
    public final int points;
    public final int amount;

    Character(char character, int points, int amount) {
        this.character = character;
        this.points = points;
        this.amount = amount;
    }

    public String getText() {
        return String.valueOf(this.character).toUpperCase();
    }

    public static Character byCharacter(String character) {
        for (var c : Character.values()) {
            if (c.getText().equals(character.toUpperCase())) {
                return c;
            }
        }

        return null;
    }

    public static Character random() {
        var random = new Random();
        var characters = Character.values();

        return characters[random.nextInt(characters.length)];
    }
}
