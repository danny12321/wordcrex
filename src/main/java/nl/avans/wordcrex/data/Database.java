package nl.avans.wordcrex.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private final DataSource source;

    public Database(DataSource source) {
        this.source = source;
    }

    public static Database connect(String configName) {
        var config = new HikariConfig("/db/" + configName + ".properties");
        var source = new HikariDataSource(config);

        return new Database(source);
    }

    public Connection getConnection() throws SQLException {
        return this.source.getConnection();
    }

    public boolean select(String sql, SqlConsumer<PreparedStatement> prepare, SqlConsumer<ResultSet> consumer) {
        var results = false;

        try (var connection = this.getConnection();
             var statement = connection.prepareStatement(sql)) {
            prepare.accept(statement);

            try (var result = statement.executeQuery()) {
                if (results = result.next()) {
                    do {
                        consumer.accept(result);
                    } while (result.next());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public int update(String sql, SqlConsumer<PreparedStatement> prepare) {
        var updated = 0;

        try (var connection = this.getConnection();
             var statement = connection.prepareStatement(sql)) {
            prepare.accept(statement);

            updated = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updated;
    }
}
