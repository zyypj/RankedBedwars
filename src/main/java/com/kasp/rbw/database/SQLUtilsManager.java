package com.kasp.rbw.database;

import com.kasp.rbw.PickingMode;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtilsManager {

    public static void createRank(String ID, String startingElo, String endingElo, String winElo, String loseElo, String mvpElo) {
        SQLite.updateData("INSERT INTO ranks(discordID, startingElo, endingElo, winElo, loseElo, mvpElo)" +
                " VALUES('" + ID + "'," +
                startingElo + "," +
                endingElo + "," +
                winElo + "," +
                loseElo + "," +
                mvpElo + ");");
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
        SQLite.updateData("INSERT INTO queues(discordID, playersEachTeam, pickingMode, casual, eloMultiplier)" +
                " VALUES('" + ID + "'," +
                playersEachTeam + "," +
                "'" + pickingMode.toString().toUpperCase() + "'," +
                "'" + casual + "'," +
                "1.0);");
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
