package nl.avans.wordcrex.model;

import java.util.Random;

public enum Character {
    A('a', 2),
    B('b', 3),
    C('c', 6),
    D('d', 2),
    E('e', 1),
    F('f', 6),
    G('g', 4),
    H('h', 5),
    I('i', 2),
    J('j', 5),
    K('k', 4),
    L('l', 4),
    M('m', 3),
    N('n', 1),
    O('o', 2),
    P('p', 5),
    Q('q', 20),
    R('r', 2),
    S('s', 3),
    T('t', 2),
    U('u', 3),
    V('v', 5),
    W('w', 6),
    X('x', 8),
    Y('y', 9),
    Z('z', 6);

    public final char character;
    public final int points;

    Character(char character, int points) {
        this.character = character;
        this.points = points;
    }

    public String getText() {
        return String.valueOf(this.character).toUpperCase();
    }

    public static Character random() {
        var random = new Random();
        var characters = Character.values();

        return characters[random.nextInt(characters.length)];
    }
}
