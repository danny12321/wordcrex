package nl.avans.wordcrex.data;

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

    public static Database connect() {
        var source = new HikariDataSource();

        // todo: change to shared db
        source.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        source.setUsername("username");
        source.setPassword("password");
        source.setMaximumPoolSize(10);
        source.addDataSourceProperty("databaseName", "wordcrex");
        source.addDataSourceProperty("portNumber", "5432");
        source.addDataSourceProperty("serverName", "localhost");

        return new Database(source);
    }

    public Connection getConnection() throws SQLException {
        return this.source.getConnection();
    }

    public boolean execute(String sql, SqlConsumer<PreparedStatement> prepare, SqlConsumer<ResultSet> consumer) {
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
}
