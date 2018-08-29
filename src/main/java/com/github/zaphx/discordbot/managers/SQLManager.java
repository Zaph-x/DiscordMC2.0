package com.github.zaphx.discordbot.managers;

import com.github.zaphx.discordbot.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SQLManager {

    private FileConfiguration config = Main.getInstance().getConfig();
    private static SQLManager instance;
    private String prefix = config.getString("sql.prefix");
    private long ticketWarning; // TODO use this
    private long ticketMute;

    private final int PORT = config.getInt("sql.port");
    private final String USERNAME = config.getString("sql.username");
    private final String PASSWORD = config.getString("sql.password");
    private final String HOST = config.getString("sql.host");
    private final String DATABASE = config.getString("sql.database");

    // Not public constructor
    private SQLManager() {
    }

    // Return our instance
    public static SQLManager getInstance() {
        return instance == null ? instance = new SQLManager() : instance;
    }


    // Create the connection to the SQL server
    @NotNull
    private Connection getConnection() {
        String driver = "com.mysql.jdbc.Driver";
        String url = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);

        try {
            // Check if driver exists
            Class.forName(driver);
            return DriverManager.getConnection(url + "?useSSL=false", USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.print("An error occurred while establishing connection to the SQL server. See stacktrace below for more information.");
            e.printStackTrace();
        }
        // Should never happen
        return null;
    }

    // Check if the given table exists
    public boolean tableExist(String tableName) {
        Connection connection = getConnection();
        boolean tExists = false;
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equals(config.getString("sql.prefix") + tableName)) {
                    tExists = true;
                    break;
                }
            }
            connection.close();
        } catch (SQLException e) {
            System.err.print("An error occurred while checking if a table exists in your database. See stacktrace below for more information.");
            e.printStackTrace();
        }
        // Close connection to prevent too many open connections
        return tExists;
    }

    public void executeStatementAndPost(@Language("sql") String sql, Object... parameters) {
        Future<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareCall(String.format(sql, parameters));
                ps.execute();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public long countTickets(String table) {
        Future<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareCall("SELECT COUNT(ticket) AS size FROM " + prefix + table);
                List<Long> list = new ArrayList<>();
                ResultSet set = ps.executeQuery();
                while (set.next()) {
                    list.add(set.getLong("size"));
                }
                connection.close();
                return list.get(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0L;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;

    }

    public void createWarningsIfNotExists() {
        Future<Boolean> future = CompletableFuture.supplyAsync(() -> {
            if (!tableExist("warnings")) {
                try {
                    Connection connection = getConnection();
                    connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + prefix + "warnings (\n" +
                            "ticket INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, \n" +
                            "id BIGINT UNSIGNED NOT NULL,\n" +
                            "reason VARCHAR(255) NOT NULL, \n" +
                            "warnee BIGINT UNSIGNED NOT NULL" +
                            ")");
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void createMutesIfNotExists() {
        Future<Boolean> future = CompletableFuture.supplyAsync(() -> {
            Connection connection = getConnection();
            if (!tableExist("mutes")) {
                try {
                    String prefix = config.getString("sql.prefix");
                    connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + prefix + "mutes (\n" +
                            "ticket INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, \n" +
                            "id BIGINT UNSIGNED NOT NULL, \n" +
                            "time DATETIME NOT NULL DEFAULT NOW(), \n" +
                            "muter BIGINT UNSIGNED NOT NULL, \n" +
                            "expires BIGINT UNSIGNED NOT NULL" +
                            ")");
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void createRemindersIfNotExists() {
        Future<Boolean> future = CompletableFuture.supplyAsync(() -> {
            Connection connection = getConnection();
            if (!tableExist("reminders")) {
                try {
                    String prefix = config.getString("sql.prefix");
                    connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + prefix + "reminders (\n" +
                            "ticket INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, \n" +
                            "id BIGINT UNSIGNED NOT NULL, \n" +
                            "time DATETIME NOT NULL DEFAULT NOW(), \n" +
                            "remind VARCHAR(255) NOT NULL, \n" +
                            "expires BIGINT UNSIGNED NOT NULL" +
                            ")");
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return true;
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void countTickets() {
        this.ticketMute = countTickets("mutes");
        this.ticketWarning = countTickets("warnings");
    }
}