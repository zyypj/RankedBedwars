package me.zypj.rbw.database;

import me.zypj.rbw.instance.ClanWar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLClanWars {

    public static void createWar(int playersPerTeam, int minClans, int maxClans, int xpPerWin, int goldPerWin) {
        String sql = "INSERT INTO clanwars(playersPerTeam, minClans, maxClans, xpPerWin, goldPerWin, active) VALUES(?,?,?,?,?,?)";
        SQLite.updateData(sql, playersPerTeam, minClans, maxClans, xpPerWin, goldPerWin, "true");
    }

    public static void registerClan(String warId, String clanName) {
        String sql = "INSERT INTO clanwar_registrations(warId, clanName) VALUES(?,?)";
        SQLite.updateData(sql, warId, clanName);
    }

    public static void unregisterClan(String warId, String clanName) {
        String sql = "DELETE FROM clanwar_registrations WHERE warId=? AND clanName=?";
        SQLite.updateData(sql, warId, clanName);
    }

    public static void updateWarStatus(String warId, boolean isActive) {
        String sql = "UPDATE clanwars SET active = ? WHERE warId=?";
        SQLite.updateData(sql, isActive, warId);
    }

    public static List<String> getRegisteredClans(String warId) {
        ResultSet resultSet = SQLite.queryData("SELECT clanName FROM clanwar_registrations WHERE warId=?", warId);
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
        ResultSet resultSet = SQLite.queryData("SELECT * FROM clanwars WHERE warId=?", warId);
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

