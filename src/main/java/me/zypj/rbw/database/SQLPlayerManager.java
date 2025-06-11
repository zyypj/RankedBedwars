package me.zypj.rbw.database;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.Theme;
import me.zypj.rbw.instance.cache.PlayerCache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class SQLPlayerManager {

    public static void createPlayer(String ID, String ign) {
        String sql = "INSERT INTO players(discordID, ign, elo, peakElo, wins, losses, winStreak, lossStreak, highestWS, highestLS," +
                " mvp, kills, deaths, strikes, scored, gold, level, xp, theme, ownedThemes, isBanned, bannedTill)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLite.updateData(sql,
                ID,
                ign,
                Integer.parseInt(Config.getValue("starting-elo")),
                Integer.parseInt(Config.getValue("starting-elo")),
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                "default",
                "default",
                "false",
                "");
    }

    public static void unregisterPlayer(String ID) {
        SQLite.updateData("DELETE FROM players WHERE discordID = ?", ID);
    }

    public static int getPlayerSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(discordID) FROM players");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLite.closeResultSet(resultSet);
        }

        return 0;
    }

    public static boolean isRegistered(String ID) {
        ResultSet resultSet = SQLite.queryData("SELECT EXISTS(SELECT 1 FROM players WHERE discordID=?)", ID);
        try {
            if (resultSet.getInt(1) == 1)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLite.closeResultSet(resultSet);
        }

        return false;
    }

    public static void updateIgn(String ID) {
        SQLite.updateData("UPDATE players SET ign = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getIgn(), ID);
    }

    public static void updateElo(String ID) {
        SQLite.updateData("UPDATE players SET elo = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getElo(), ID);
    }

    public static void updatePeakElo(String ID) {
        SQLite.updateData("UPDATE players SET peakElo = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getPeakElo(), ID);
    }

    public static void updateWins(String ID) {
        SQLite.updateData("UPDATE players SET wins = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getWins(), ID);
    }

    public static void updateLosses(String ID) {
        SQLite.updateData("UPDATE players SET losses = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getLosses(), ID);
    }

    public static void updateWinStreak(String ID) {
        SQLite.updateData("UPDATE players SET winStreak = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getWinStreak(), ID);
    }

    public static void updateLossStreak(String ID) {
        SQLite.updateData("UPDATE players SET lossStreak = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getLossStreak(), ID);
    }

    public static void updateHighestWS(String ID) {
        SQLite.updateData("UPDATE players SET highestWS = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getHighestWS(), ID);
    }

    public static void updateHighestLS(String ID) {
        SQLite.updateData("UPDATE players SET highestLS = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getHighestLS(), ID);
    }

    public static void updateMvp(String ID) {
        SQLite.updateData("UPDATE players SET mvp = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getMvp(), ID);
    }

    public static void updateKills(String ID) {
        SQLite.updateData("UPDATE players SET kills = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getKills(), ID);
    }

    public static void updateDeaths(String ID) {
        SQLite.updateData("UPDATE players SET deaths = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getDeaths(), ID);
    }

    public static void updateStrikes(String ID) {
        SQLite.updateData("UPDATE players SET strikes = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getStrikes(), ID);
    }

    public static void updateScored(String ID) {
        SQLite.updateData("UPDATE players SET scored = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getScored(), ID);
    }

    public static void updateGold(String ID) {
        SQLite.updateData("UPDATE players SET gold = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getGold(), ID);
    }

    public static void updateLevel(String ID) {
        SQLite.updateData("UPDATE players SET level = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getLevel().getLevel(), ID);
    }

    public static void updateXP(String ID) {
        SQLite.updateData("UPDATE players SET xp = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getXp(), ID);
    }

    public static void updateTheme(String ID) {
        SQLite.updateData("UPDATE players SET theme = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getTheme().getName(), ID);
    }

    public static void updateOwnedThemes(String ID) {
        Player player = PlayerCache.getPlayer(ID);

        StringBuilder themes = new StringBuilder();
        for (Theme t : player.getOwnedThemes()) {
            themes.append(t.getName());
            if (player.getOwnedThemes().indexOf(t) != player.getOwnedThemes().size() - 1) {
                themes.append(",");
            }
        }

        SQLite.updateData("UPDATE players SET ownedThemes = ? WHERE discordID=?", themes.toString(), ID);
    }

    public static void updateIsBanned(String ID) {
        SQLite.updateData("UPDATE players SET isBanned = ? WHERE discordID=?", PlayerCache.getPlayer(ID).isBanned(), ID);
    }

    public static void updateBannedTill(String ID) {
        Player player = PlayerCache.getPlayer(ID);

        if (player.isBanned()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String bannedTill = formatter.format(player.getBannedTill());

            SQLite.updateData("UPDATE players SET bannedTill = ? WHERE discordID=?", bannedTill, ID);
        } else {
            SQLite.updateData("UPDATE players SET bannedTill = '' WHERE discordID=?", ID);
        }
    }

    public static void updateBanReason(String ID) {
        Player player = PlayerCache.getPlayer(ID);

        if (player.isBanned()) {
            SQLite.updateData("UPDATE players SET banReason = ? WHERE discordID=?", PlayerCache.getPlayer(ID).getBanReason(), ID);
        } else {
            SQLite.updateData("UPDATE players SET banReason = '' WHERE discordID=?", ID);
        }
    }
}

