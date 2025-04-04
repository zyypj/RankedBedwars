package com.kasp.rbw.instance;

import com.kasp.rbw.sample.Statistic;
import com.kasp.rbw.instance.cache.ClanCache;
import com.kasp.rbw.instance.cache.PlayerCache;

import java.util.*;

public class Leaderboard {

    public static List<String> getLeaderboard(Statistic statistic) {
        Map<Player, Double> unsortedMap = new HashMap<>();

        for (Player p : PlayerCache.getPlayers().values()) {

            unsortedMap.put(p, p.getStatistic(statistic));
        }

        List<String> lb = new ArrayList<>();

        List<Map.Entry<Player, Double>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<Player, Double> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<Player, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Player, Double> entry : sortedMap.entrySet()) {
            lb.add(entry.getKey().getID() + "=" + entry.getValue());
        }

        return lb;
    }

    public static List<Clan> getClansLeaderboard() {
        Map<Clan, Integer> unsortedMap = new HashMap<>();

        for (Clan c : ClanCache.getClans().values()) {

            unsortedMap.put(c, c.getReputation());
        }

        List<Map.Entry<Clan, Integer>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<Clan, Integer> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<Clan, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        List<Clan> lb = new ArrayList<>();

        for (Map.Entry<Clan, Integer> entry : sortedMap.entrySet()) {
            lb.add(entry.getKey());
        }

        return lb;
    }
}
