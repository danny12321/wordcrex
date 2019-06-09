package nl.avans.wordcrex.util;

import nl.avans.wordcrex.model.Wordcrex;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class StreamUtil {
    public static <T> Function<Wordcrex, T> getModelProperty(Function<Wordcrex, List<T>> fn, Predicate<T> predicate) {
        return (user) -> {
            var stream = fn.apply(user).stream();

            return stream.filter(predicate).findFirst().orElse(null);
        };
    }
}
