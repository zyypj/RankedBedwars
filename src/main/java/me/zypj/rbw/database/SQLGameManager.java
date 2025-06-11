package me.zypj.rbw.database;

import me.zypj.rbw.instance.Game;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.GameCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGameManager {

    public static void createGame(Game g) {
        String sql = "INSERT INTO games(number, state, casual, map, channelID, vc1ID, vc2ID, queueID, team1, team2) VALUES(?,?,?,?,?,?,?,?,?,?)";
        SQLite.updateData(sql,
                g.getNumber(),
                g.getState().toString().toUpperCase(),
                String.valueOf(g.isCasual()),
                g.getMap() == null ? null : g.getMap().getName(),
                g.getChannelID(),
                g.getVC1ID(),
                g.getVC2ID(),
                g.getQueue().getID(),
                "",
                "");
    }

    public static void updateGame(Game g) {
        StringBuilder team1 = new StringBuilder();
        StringBuilder team2 = new StringBuilder();

        for (Player p : g.getTeam1()) {
            team1.append(p.getID());
            if (g.getTeam1().indexOf(p) + 1 < g.getTeam1().size()) {
                team1.append(",");
            }
        }

        for (Player p : g.getTeam2()) {
            team2.append(p.getID());
            if (g.getTeam2().indexOf(p) + 1 < g.getTeam2().size()) {
                team2.append(",");
            }
        }

        ResultSet resultSet = SQLite.queryData("SELECT EXISTS(SELECT 1 FROM games WHERE number=?)", g.getNumber());
        try {
            if (resultSet.getInt(1) == 1) {
                SQLite.updateData("DELETE FROM games WHERE number=?", g.getNumber());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLite.closeResultSet(resultSet);
        }

        String sql = "INSERT INTO games(number, state, casual, map, channelID, vc1ID, vc2ID, queueID, team1, team2) VALUES(?,?,?,?,?,?,?,?,?,?)";
        SQLite.updateData(sql,
                g.getNumber(),
                g.getState().toString().toUpperCase(),
                String.valueOf(g.isCasual()),
                g.getMap().getName(),
                g.getChannelID(),
                g.getVC1ID(),
                g.getVC2ID(),
                g.getQueue().getID(),
                team1.toString(),
                team2.toString());
    }

    public static int getGameSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(number) FROM games");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SQLite.closeResultSet(resultSet);
        }

        return 0;
    }

    public static void updateState(int number) {
        String sql = "UPDATE games SET state = ? WHERE number=?";
        SQLite.updateData(sql, GameCache.getGame(number).getState().toString().toUpperCase(), number);
    }

    public static void updateMvp(int number) {
        String sql = "UPDATE games SET mvp = ? WHERE number=?";
        SQLite.updateData(sql, GameCache.getGame(number).getMvp().getIgn(), number);
    }

    public static void updateScoredBy(int number) {
        String sql = "UPDATE games SET scoredBy = ? WHERE number=?";
        SQLite.updateData(sql, GameCache.getGame(number).getScoredBy().getId(), number);
    }

    public static void updateEloGain(int number) {
        Game g = GameCache.getGame(number);

        StringBuilder team1 = new StringBuilder();
        StringBuilder team2 = new StringBuilder();

        for (Player p : g.getTeam1()) {
            team1.append(p.getID()).append("=").append(g.getEloGain().get(p));
            if (g.getTeam1().indexOf(p) + 1 < g.getTeam1().size()) {
                team1.append(",");
            }
        }

        for (Player p : g.getTeam2()) {
            team2.append(p.getID()).append("=").append(g.getEloGain().get(p));
            if (g.getTeam2().indexOf(p) + 1 < g.getTeam2().size()) {
                team2.append(",");
            }
        }

        SQLite.updateData("UPDATE games SET team1 = ? WHERE number=?", team1.toString(), number);
        SQLite.updateData("UPDATE games SET team2 = ? WHERE number=?", team2.toString(), number);
    }

    public static void removeEloGain(int number) {
        Game g = GameCache.getGame(number);

        StringBuilder team1 = new StringBuilder();
        StringBuilder team2 = new StringBuilder();

        for (Player p : g.getTeam1()) {
            team1.append(p.getID());
            if (g.getTeam1().indexOf(p) + 1 < g.getTeam1().size()) {
                team1.append(",");
            }
        }

        for (Player p : g.getTeam2()) {
            team2.append(p.getID());
            if (g.getTeam2().indexOf(p) + 1 < g.getTeam2().size()) {
                team2.append(",");
            }
        }

        SQLite.updateData("UPDATE games SET team1 = ? WHERE number=?", team1.toString(), number);
        SQLite.updateData("UPDATE games SET team2 = ? WHERE number=?", team2.toString(), number);
    }
}

