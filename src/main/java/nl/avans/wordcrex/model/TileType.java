package nl.avans.wordcrex.model;

import nl.avans.wordcrex.util.Pair;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TileType {
    NONE("^--$", (match) -> 1),
    LETTER("^([0-9]+)L$", (match) -> Integer.parseInt(match.group(1))),
    WORD("^([0-9]+)W$", (match) -> Integer.parseInt(match.group(1))),
    CENTER("^\\*$", (match) -> 3);

    public final Pattern type;
    public final Function<Matcher, Integer> multiplier;

    TileType(String type, Function<Matcher, Integer> multiplier) {
        this.type = Pattern.compile(type);
        this.multiplier = multiplier;
    }

    public static Pair<TileType, Integer> byType(String type) {
        for (var t : TileType.values()) {
            var match = t.type.matcher(type);

            if (match.matches()) {
                return new Pair<>(t, t.multiplier.apply(match));
            }
        }

        throw new RuntimeException("Invalid tile type: " + type);
    }
}
