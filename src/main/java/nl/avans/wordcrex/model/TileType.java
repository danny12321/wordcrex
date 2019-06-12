package nl.avans.wordcrex.model;

import nl.avans.wordcrex.util.Pair;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TileType {
    NONE("-", "^--$", (match) -> 1),
    LETTER("L", "^([0-9]+)L$", (match) -> Integer.parseInt(match.group(1))),
    WORD("W", "^([0-9]+)W$", (match) -> Integer.parseInt(match.group(1))),
    CENTER("*", "^\\*$", (match) -> 3);

    public final String type;
    public final Pattern pattern;
    public final Function<Matcher, Integer> multiplier;

    TileType(String type, String pattern, Function<Matcher, Integer> multiplier) {
        this.type = type;
        this.pattern = Pattern.compile(pattern);
        this.multiplier = multiplier;
    }

    public static Pair<TileType, Integer> byType(String type) {
        for (var t : TileType.values()) {
            var match = t.pattern.matcher(type);

            if (match.matches()) {
                return new Pair<>(t, t.multiplier.apply(match));
            }
        }

        throw new RuntimeException("Invalid tile type: " + type);
    }
}
