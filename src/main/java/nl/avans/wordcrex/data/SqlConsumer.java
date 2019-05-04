package nl.avans.wordcrex.data;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<T> {
    void accept(T t) throws SQLException;
}
