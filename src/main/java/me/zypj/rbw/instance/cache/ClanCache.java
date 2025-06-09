package me.zypj.rbw.instance.cache;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Clan;
import me.zypj.rbw.instance.Player;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ClanCache {

    @Getter
    private static final Map<String, Clan> clans = new HashMap<>();

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

    public static void initializeClan(String name, Clan clan) {
        if (!containsClan(name))
            addClan(clan);

        getClan(name);
    }
}
