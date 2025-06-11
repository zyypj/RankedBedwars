package me.zypj.rbw.database;

import me.zypj.rbw.instance.cache.ClanCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLClanManager {

    public static void createClan(String name, String leader) {
        String sql = "INSERT INTO clans(name, leader, members, reputation, clanXP, clanLevel, wins, losses, private, eloJoinReq, description)"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        SQLite.updateData(sql,
                name,
                leader,
                leader,
                0,
                0,
                0,
                0,
                0,
                "true",
                0,
                "A newly created RBW clan");
    }

    public static int getClanSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(name) FROM clans");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void updateReputation(String name) {
        String sql = "UPDATE clans SET reputation = ? WHERE name=?";
        SQLite.updateData(sql, ClanCache.getClan(name).getReputation(), name);
    }

    public static void updateXP(String name) {
        String sql = "UPDATE clans SET xp = ? WHERE name=?";
        SQLite.updateData(sql, ClanCache.getClan(name).getXp(), name);
    }

    public static void updateLevel(String name) {
        String sql = "UPDATE clans SET level = ? WHERE name=?";
        SQLite.updateData(sql, ClanCache.getClan(name).getLevel().getLevel(), name);
    }

    public static void updatePrivate(String name) {
        String sql = "UPDATE clans SET private = ? WHERE name=?";
        SQLite.updateData(sql, ClanCache.getClan(name).isPrivate(), name);
    }

    public static void updateEloJoinReq(String name) {
        String sql = "UPDATE clans SET eloJoinReq = ? WHERE name=?";
        SQLite.updateData(sql, ClanCache.getClan(name).getEloJoinReq(), name);
    }

    public static void updateDescription(String name) {
        String sql = "UPDATE clans SET description = ? WHERE name=?";
        SQLite.updateData(sql, ClanCache.getClan(name).getDescription(), name);
    }
}

