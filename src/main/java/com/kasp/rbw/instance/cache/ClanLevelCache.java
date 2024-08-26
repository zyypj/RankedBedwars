package com.kasp.rbw.instance.cache;

import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.ClanLevel;

import java.util.HashMap;
import java.util.Map;

public class ClanLevelCache {

    private static Map<Integer, ClanLevel> clanLevels = new HashMap<>();

    public static ClanLevel getLevel(int level) {
        return clanLevels.get(level);
    }

    public static void addLevel(ClanLevel level) {
        clanLevels.put(level.getLevel(), level);

        Config.debug("ClanLevel " + level.getLevel() + " foi carregado na memoria");
    }

    public static void removeLevel(int level) {
        clanLevels.remove(level);
    }

    public static boolean containsLevel(int level) {
        return clanLevels.containsKey(level);
    }

    public static ClanLevel initializeLevel(int level, ClanLevel lvlobj) {
        if (!containsLevel(level))
            addLevel(lvlobj);

        return getLevel(level);
    }

    public static Map<Integer, ClanLevel> getClanLevels() {
        return clanLevels;
    }
}
