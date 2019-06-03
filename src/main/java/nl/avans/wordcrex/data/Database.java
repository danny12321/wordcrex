package nl.avans.wordcrex.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class Database {
    private final DataSource source;

    public Database(String config) {
        this.source = new HikariDataSource(new HikariConfig("/db/" + config + ".properties"));
    }

    private Connection getConnection() throws SQLException {
        return this.source.getConnection();
    }

    public int select(String sql, SqlConsumer<ResultSet> consumer) {
        return this.select(sql, (statement) -> {}, consumer);
    }

    public int select(String sql, SqlConsumer<PreparedStatement> prepare, SqlConsumer<ResultSet> consumer) {
        var selected = 0;

        try (var connection = this.getConnection();
             var statement = connection.prepareStatement(sql)) {
            prepare.accept(statement);

            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    consumer.accept(result);
                    selected++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return selected;
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

    public int insert(String sql, SqlConsumer<PreparedStatement> prepare) {
        try (var connection = this.getConnection();
             var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepare.accept(statement);

            var updated = statement.executeUpdate();

            if (updated == 0) {
                return -1;
            }

            try (var result = statement.getGeneratedKeys()) {
                if (!result.next()) {
                    return 0;
                }

                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
