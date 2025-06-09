package me.zypj.rbw.database;

import me.zypj.rbw.instance.Game;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.GameCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGameManager {

    public static void createGame(Game g) {
        String team1 = "";
        String team2 = "";

        SQLite.updateData("INSERT INTO games(number, state, casual, map, channelID, vc1ID, vc2ID, queueID, team1, team2)" +
                " VALUES(" + g.getNumber() + "," +
                "'" + g.getState().toString().toUpperCase() + "'," +
                "'" + g.isCasual() + "'," +
                "'" + (g.getMap() == null ? null : g.getMap().getName()) + "'," +
                "'" + g.getChannelID() + "'," +
                "'" + g.getVC1ID() + "'," +
                "'" + g.getVC2ID() + "'," +
                "'" + g.getQueue().getID() + "'," +
                "'" + team1 + "'," +
                "'" + team2 + "');");
    }

    public static void updateGame(Game g) {
        StringBuilder team1 = new StringBuilder();
        StringBuilder team2 = new StringBuilder();

        for (Player p : g.getTeam1()) {
            team1.append(p.getID());
            if (g.getTeam1().indexOf(p)+1 < g.getTeam1().size()) {
                team1.append(",");
            }
        }

        for (Player p : g.getTeam2()) {
            team2.append(p.getID());
            if (g.getTeam2().indexOf(p)+1 < g.getTeam2().size()) {
                team2.append(",");
            }
        }

        ResultSet resultSet = SQLite.queryData("SELECT EXISTS(SELECT 1 FROM games WHERE number='" + g.getNumber() + "');");
        try {
            if (resultSet.getInt(1) == 1)
                SQLite.updateData("DELETE FROM games WHERE number = " + g.getNumber() + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SQLite.updateData("INSERT INTO games(number, state, casual, map, channelID, vc1ID, vc2ID, queueID, team1, team2)" +
                " VALUES(" + g.getNumber() + "," +
                "'" + g.getState().toString().toUpperCase() + "'," +
                "'" + g.isCasual() + "'," +
                "'" + g.getMap().getName() + "'," +
                "'" + g.getChannelID() + "'," +
                "'" + g.getVC1ID() + "'," +
                "'" + g.getVC2ID() + "'," +
                "'" + g.getQueue().getID() + "'," +
                "'" + team1 + "'," +
                "'" + team2 + "');");
    }

    public static int getGameSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(number) FROM games");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void updateState(int number) {
        SQLite.updateData("UPDATE games SET state = '" + GameCache.getGame(number).getState().toString().toUpperCase() + "' WHERE number=" + number + ";");
    }

    public static void updateMvp(int number) {
        SQLite.updateData("UPDATE games SET mvp = '" + GameCache.getGame(number).getMvp().getIgn() + "' WHERE number=" + number + ";");
    }

    public static void updateScoredBy(int number) {
        SQLite.updateData("UPDATE games SET scoredBy = '" + GameCache.getGame(number).getScoredBy().getId() + "' WHERE number=" + number + ";");
    }

    public static void updateEloGain(int number) {
        Game g = GameCache.getGame(number);

        StringBuilder team1 = new StringBuilder();
        StringBuilder team2 = new StringBuilder();

        for (Player p : g.getTeam1()) {
            team1.append(p.getID()).append("=").append(g.getEloGain().get(p));
            if (g.getTeam1().indexOf(p)+1 < g.getTeam1().size()) {
                team1.append(",");
            }
        }

        for (Player p : g.getTeam2()) {
            team2.append(p.getID()).append("=").append(g.getEloGain().get(p));
            if (g.getTeam2().indexOf(p)+1 < g.getTeam2().size()) {
                team2.append(",");
            }
        }

        SQLite.updateData("UPDATE games SET team1 = '" + team1 + "' WHERE number=" + number + ";");
        SQLite.updateData("UPDATE games SET team2 = '" + team2 + "' WHERE number=" + number + ";");
    }

    public static void removeEloGain(int number) {
        Game g = GameCache.getGame(number);

        StringBuilder team1 = new StringBuilder();
        StringBuilder team2 = new StringBuilder();

        for (Player p : g.getTeam1()) {
            team1.append(p.getID());
            if (g.getTeam1().indexOf(p)+1 < g.getTeam1().size()) {
                team1.append(",");
            }
        }

        for (Player p : g.getTeam2()) {
            team2.append(p.getID());
            if (g.getTeam2().indexOf(p)+1 < g.getTeam2().size()) {
                team2.append(",");
            }
        }

        SQLite.updateData("UPDATE games SET team1 = '" + team1 + "' WHERE number=" + number + ";");
        SQLite.updateData("UPDATE games SET team2 = '" + team2 + "' WHERE number=" + number + ";");
    }
}
