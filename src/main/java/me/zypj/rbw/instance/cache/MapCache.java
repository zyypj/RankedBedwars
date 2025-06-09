package me.zypj.rbw.instance.cache;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.GameMap;

import java.util.HashMap;
import java.util.Map;

public class MapCache {

    private static final HashMap<String, GameMap> maps = new HashMap<>();

    public static GameMap getMap(String name) {
        return maps.get(name);
    }

    public static void addMap(GameMap map) {
        maps.put(map.getName(), map);

        Config.debug("Map " + map.getName() + " foi carregado na memoria");
    }

    public static void removeMap(GameMap map) {
        maps.remove(map.getName());
    }

    public static boolean containsMap(String name) {
        return maps.containsKey(name);
    }

    public static void initializeMap(String name, GameMap map) {
        if (!containsMap(name))
            addMap(map);

        getMap(name);
    }

    public static Map<String, GameMap> getMaps() {
        return maps;
    }
}
