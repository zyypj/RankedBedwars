package me.zypj.rbw.instance.cache;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.ClanLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ClanLevelCache {

    @Getter
    private static Map<Integer, ClanLevel> clanLevels = new HashMap<>();

    public static ClanLevel getLevel(int level) {
        return clanLevels.get(level);
    }

    public static void addLevel(ClanLevel level) {
        clanLevels.put(level.getLevel(), level);

        Config.debug("ClanLevel " + level.getLevel() + " has loaded");
    }

    public static void removeLevel(int level) {
        clanLevels.remove(level);
    }

    public static boolean containsLevel(int level) {
        return clanLevels.containsKey(level);
    }

    public static void initializeLevel(int level, ClanLevel lvlobj) {
        if (!containsLevel(level))
            addLevel(lvlobj);

        getLevel(level);
    }

}
