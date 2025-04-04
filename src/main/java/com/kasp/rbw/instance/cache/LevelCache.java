package com.kasp.rbw.instance.cache;

import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Level;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class LevelCache {

    @Getter
    private static Map<Integer, Level> levels = new HashMap<>();

    public static Level getLevel(int level) {
        return levels.get(level);
    }

    public static void addLevel(Level level) {
        levels.put(level.getLevel(), level);

        Config.debug("Level" + level.getLevel() + " foi carregado na memoria");
    }

    public static void removeLevel(int level) {
        levels.remove(level);
    }

    public static boolean containsLevel(int level) {
        return levels.containsKey(level);
    }

    public static void initializeLevel(int level, Level lvlobj) {
        if (!containsLevel(level))
            addLevel(lvlobj);

        getLevel(level);
    }

}
