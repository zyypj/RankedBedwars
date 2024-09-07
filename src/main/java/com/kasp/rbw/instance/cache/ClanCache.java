package com.kasp.rbw.instance.cache;

import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Clan;
import com.kasp.rbw.instance.Player;

import java.util.HashMap;
import java.util.Map;

public class ClanCache {

    private static Map<String, Clan> clans = new HashMap<>();

    public static Clan getClan(String name) {
        return clans.values().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Clan getClan(Player player) {
        return clans.values().stream().filter(c -> c.getMembers().contains(player)).findFirst().orElse(null);
    }

    public static void addClan(Clan clan) {
        clans.put(clan.getName(), clan);

        Config.debug("Clan" + clan.getName() + " foi carregado na memoria!");
    }

    public static void removeClan(String name) {
        clans.remove(name);
    }

    public static boolean containsClan(String name) {
        return clans.containsKey(name);
    }

    public static Clan initializeClan(String name, Clan clan) {
        if (!containsClan(name))
            addClan(clan);

        return getClan(name);
    }

    public static Map<String, Clan> getClans() {
        return clans;
    }
}
