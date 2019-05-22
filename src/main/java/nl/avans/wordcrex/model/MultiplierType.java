package nl.avans.wordcrex.model;

public enum MultiplierType {
    WORD('W'),
    LETTER('L');

    public final char type;

    MultiplierType(char type) {
        this.type = type;
    }

    public static MultiplierType byType(char type) {
        for (var t : MultiplierType.values()) {
            if (t.type == type) {
                return t;
            }
        }

        return null;
    }
}
