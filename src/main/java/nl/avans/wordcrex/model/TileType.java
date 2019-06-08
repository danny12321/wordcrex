package nl.avans.wordcrex.model;

import nl.avans.wordcrex.util.Pair;

import java.util.function.Function;

public enum TileType {
    NONE("--", (type) -> 1),
    LETTER("L", (type) -> Integer.parseInt(type.substring(1))),
    WORD("W", (type) -> Integer.parseInt(type.substring(1))),
    CENTER("*", (type) -> 3);

    public final String type;
    public final Function<String, Integer> multiplier;

    TileType(String type, Function<String, Integer> multiplier) {
        this.type = type;
        this.multiplier = multiplier;
    }

    public static Pair<TileType, Integer> byType(String type) {
        for (var t : TileType.values()) {
            if (type.startsWith(t.type)) {
                return new Pair<>(t, t.multiplier.apply(type));
            }
        }

        throw new RuntimeException("Invalid tile type: " + type);
    }
}
