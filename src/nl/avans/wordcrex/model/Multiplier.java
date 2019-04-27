package nl.avans.wordcrex.model;

public enum Multiplier {
    L2(Type.Character, 2),
    L4(Type.Character, 4),
    L6(Type.Character, 6),
    W3(Type.Word, 3),
    W4(Type.Word, 4);

    public final Type type;
    public final int multiplier;

    Multiplier(Type type, int multiplier) {
        this.type = type;
        this.multiplier = multiplier;
    }

    public enum Type {
        Word,
        Character
    }
}
