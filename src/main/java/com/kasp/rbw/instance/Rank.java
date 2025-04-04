package com.kasp.rbw.instance;

import com.kasp.rbw.database.SQLite;
import com.kasp.rbw.instance.cache.RankCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Rank {

    private String ID;
    private int startingElo;
    private int endingElo;
    private int winElo;
    private int loseElo;
    private int mvpElo;

    public Rank(String ID) {
        this.ID = ID;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM ranks WHERE discordID='" + ID + "';");

        try {
            this.startingElo = resultSet.getInt(2);
            this.endingElo = resultSet.getInt(3);
            this.winElo = resultSet.getInt(4);
            this.loseElo = resultSet.getInt(5);
            this.mvpElo = resultSet.getInt(6);
            
            SQLite.closeResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RankCache.initializeRank(ID, this);
    }

    public static void delete(String ID) {
        RankCache.removeRank(RankCache.getRank(ID));

        SQLite.updateData("DELETE FROM ranks WHERE discordID='" + ID + "';");
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public int getStartingElo() {
        return startingElo;
    }
    public void setStartingElo(int startingElo) {
        this.startingElo = startingElo;
    }
    public int getEndingElo() {
        return endingElo;
    }
    public void setEndingElo(int endingElo) {
        this.endingElo = endingElo;
    }
    public int getWinElo() {
        return winElo;
    }
    public void setWinElo(int winElo) {
        this.winElo = winElo;
    }
    public int getLoseElo() {
        return loseElo;
    }
    public void setLoseElo(int loseElo) {
        this.loseElo = loseElo;
    }
    public int getMvpElo() {
        return mvpElo;
    }
    public void setMvpElo(int mvpElo) {
        this.mvpElo = mvpElo;
    }
}
