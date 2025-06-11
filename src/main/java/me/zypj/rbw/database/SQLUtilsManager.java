package me.zypj.rbw.database;

import me.zypj.rbw.sample.PickingMode;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtilsManager {

    public static void createRank(String ID, String startingElo, String endingElo, String winElo, String loseElo, String mvpElo) {
        String sql = "INSERT INTO ranks(discordID, startingElo, endingElo, winElo, loseElo, mvpElo) VALUES(?,?,?,?,?,?)";
        SQLite.updateData(sql, ID, startingElo, endingElo, winElo, loseElo, mvpElo);
    }

    public static int getRankSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(discordID) FROM ranks");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void createQueue(String ID, int playersEachTeam, PickingMode pickingMode, boolean casual) {
        String sql = "INSERT INTO queues(discordID, playersEachTeam, pickingMode, casual, eloMultiplier) VALUES(?,?,?,?,?)";
        SQLite.updateData(sql, ID, playersEachTeam, pickingMode.toString().toUpperCase(), String.valueOf(casual), 1.0);
    }

    public static int getQueueSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(discordID) FROM queues");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}

