package com.kasp.rbw.instance.cache;

import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCache {

    private static HashMap<String, Player> players = new HashMap<>();

    public static Player getPlayer(String ID) {
        return players.get(ID);
    }

    public static Player getPlayerByIgn(String ign) {
        for (Player p : players.values()) {
            if (p.getIgn().equalsIgnoreCase(ign)) {
                return p;
            }
        }

        return null;
    }

    public static void addPlayer(Player player) {
        players.put(player.getID(), player);

        Config.debug("Player " + player.getIgn() + " (" + player.getID() + ")" + " foi carregado na memoria");
    }

    public static void removePlayer(Player player) {
        players.remove(player.getID());
    }

    public static boolean containsPlayer(Player player) {
        return players.containsValue(player);
    }

    public static void initializePlayer(Player player) {
        if (!containsPlayer(player))
            addPlayer(player);
    }

    public static Map<String, Player> getPlayers() {
        return players;
    }
}
