package com.kasp.rbw.database;

import com.kasp.rbw.instance.ClanWar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLClanWars {

    public static void createWar(int playersPerTeam, int minClans, int maxClans, int xpPerWin, int goldPerWin) {
        SQLite.updateData("INSERT INTO clanwars(playersPerTeam, minClans, maxClans, xpPerWin, goldPerWin, active)" +
                " VALUES(" + playersPerTeam + "," +
                minClans + "," +
                maxClans + "," +
                xpPerWin + "," +
                goldPerWin + "," +
                "'true');");
    }

    public static void registerClan(String warId, String clanName) {
        SQLite.updateData("INSERT INTO clanwar_registrations(warId, clanName) VALUES(" +
                "'" + warId + "'," +
                "'" + clanName + "');");
    }

    public static void unregisterClan(String warId, String clanName) {
        SQLite.updateData("DELETE FROM clanwar_registrations WHERE warId='" + warId + "' AND clanName='" + clanName + "';");
    }

    public static void updateWarStatus(String warId, boolean isActive) {
        SQLite.updateData("UPDATE clanwars SET active = '" + isActive + "' WHERE warId='" + warId + "';");
    }

    public static List<String> getRegisteredClans(String warId) {
        ResultSet resultSet = SQLite.queryData("SELECT clanName FROM clanwar_registrations WHERE warId='" + warId + "';");
        List<String> clans = new ArrayList<>();
        try {
            while (resultSet.next()) {
                clans.add(resultSet.getString("clanName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clans;
    }

    public static ClanWar getWar(String warId) {
        ResultSet resultSet = SQLite.queryData("SELECT * FROM clanwars WHERE warId='" + warId + "';");
        try {
            if (resultSet.next()) {
                int playersPerTeam = resultSet.getInt("playersPerTeam");
                int minClans = resultSet.getInt("minClans");
                int maxClans = resultSet.getInt("maxClans");
                int xpPerWin = resultSet.getInt("xpPerWin");
                int goldPerWin = resultSet.getInt("goldPerWin");
                boolean isActive = resultSet.getBoolean("active");

                return new ClanWar(warId, playersPerTeam, minClans, maxClans, xpPerWin, goldPerWin, isActive);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}