package nl.avans.wordcrex.util;

import nl.avans.wordcrex.model.User;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class StreamUtil {
    public static <T> Function<User, T> getModelProperty(Function<User, List<T>> fn, Predicate<T> predicate) {
        return (user) -> {
            var stream = fn.apply(user).stream();

            return stream.filter(predicate).findFirst().orElse(null);
        };
    }
}
