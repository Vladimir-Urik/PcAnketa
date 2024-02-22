package lol.gggedr.pcanketa.managers.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;
import lol.gggedr.pcanketa.cons.Result;
import lol.gggedr.pcanketa.managers.Manager;
import lol.gggedr.pcanketa.managers.Managers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class DatabaseManager implements Manager {

    private final HikariConfig hikariConfig = new HikariConfig();
    private HikariPool hikariPool;

    @Override
    public void onEnable() {
        setupHikariConfig();
        setupHikariPool();
        executeSQLTableCreation();
    }

    @Override
    public void onDisable() {
        try {
            hikariPool.shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void useConnection(Consumer<Connection> consumer) {
        try (Connection connection = hikariPool.getConnection()) {
            consumer.accept(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeSQLTableCreation() {
        useConnection(connection -> {
            try (var statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS `votes` (" +
                        "`id` INT NOT NULL AUTO_INCREMENT," +
                        "`nick` VARCHAR(36) NOT NULL," +
                        "`value` VARCHAR(128) NULL," +
                        "PRIMARY KEY (`id`)," +
                        "UNIQUE KEY `nick` (`nick`)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setupHikariConfig() {
        var config = getManager(FilesManager.class).getConfig();
        var db = config.node("database");

        String host = db.node("host").getString();
        String port = db.node("port").getString();
        String database = db.node("database").getString();
        String username = db.node("username").getString();
        String password = db.node("password").getString();

        hikariConfig.setPoolName("PcAnketaConnectionPool");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true");

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.setMaximumPoolSize(6);
        hikariConfig.setMinimumIdle(3);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setConnectionTimeout(5000);
    }

    private void setupHikariPool() {
        hikariPool = new HikariPool(hikariConfig);
    }

    public List<Result> getCount() {
        var results = new ArrayList<Result>();

        useConnection(connection -> {
            try (var statement = connection.createStatement();
                 var resultSet = statement.executeQuery("SELECT value, COUNT(nick) as count, ((COUNT(nick)/(SELECT COUNT(*) FROM votes))*100) as percentage FROM votes GROUP BY value")) {
                while (resultSet.next()) {
                    var value = resultSet.getString("value");
                    var count = resultSet.getLong("count");
                    var percentage = resultSet.getFloat("percentage");
                    results.add(new Result(value, count, percentage));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return results;
    }

}
