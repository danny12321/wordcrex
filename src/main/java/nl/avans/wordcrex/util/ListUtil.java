package nl.avans.wordcrex.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListUtil {
    public static <T> List<T> reverseList(List<T> list) {
        return IntStream.range(0, list.size())
            .mapToObj((i) -> list.get(list.size() - 1 - i))
            .collect(Collectors.toList());
    }
}
