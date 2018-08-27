package com.github.zaphx.discordmc.utilities;

import com.github.zaphx.discordmc.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

public class SQLManager {

    private FileConfiguration config = Main.getInstance().getConfig();
    private static SQLManager instance;
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
        return instance == null ? new SQLManager() : instance;
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
            System.err.print("An error occurred while if a table exists in your database. See stacktrace below for more information.");
            e.printStackTrace();
        }
        // Close connection to prevent too many open connections
        return tExists;
    }


}