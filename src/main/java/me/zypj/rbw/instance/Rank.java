package me.zypj.rbw.instance;

import lombok.Getter;
import lombok.Setter;
import me.zypj.rbw.database.SQLite;
import me.zypj.rbw.instance.cache.RankCache;

import java.sql.ResultSet;
import java.sql.SQLException;

@Setter
@Getter
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
}
