package me.zypj.rbw.database;

import me.zypj.rbw.RBWPlugin;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLite {
    
    private static Connection connection;
    
    public static void connect() {
        connection = null;
        try {
            File file = new File(RBWPlugin.getInstance().getDataFolder() + "/data.db");
            if (!file.exists()) {
                file.createNewFile();
            }
            
            String link = "jdbc:sqlite:" + file.getPath();
            connection = DriverManager.getConnection(link);
            System.out.println("Successfully connected to the database");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @SneakyThrows
    public static void updateData(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ResultSet queryData(String sql) {
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    
    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                Statement statement = resultSet.getStatement();
                resultSet.close();
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
