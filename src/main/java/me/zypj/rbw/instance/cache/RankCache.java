package me.zypj.rbw.instance.cache;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Rank;

import java.util.HashMap;
import java.util.Map;

public class RankCache {

    private static final HashMap<String, Rank> ranks = new HashMap<>();

    public static Rank getRank(String ID) {
        return ranks.get(ID);
    }

    public static void addRank(Rank rank) {
        ranks.put(rank.getID(), rank);

        Config.debug("Rank " + rank.getID() + " has loaded");
    }

    public static void removeRank(Rank rank) {
        ranks.remove(rank.getID());
    }

    public static boolean containsRank(String ID) {
        return ranks.containsKey(ID);
    }

    public static void initializeRank(String ID, Rank rank) {
        if (!containsRank(ID))
            addRank(rank);

        getRank(ID);
    }

    public static Map<String, Rank> getRanks() {
        return ranks;
    }
}
