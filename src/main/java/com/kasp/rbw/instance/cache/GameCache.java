package com.kasp.rbw.instance.cache;

import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Game;

import java.util.HashMap;
import java.util.Map;

public class GameCache {

    public static HashMap<Integer, Game> games = new HashMap<>();

    public static Game getGame(int number) {
        return games.get(number);
    }

    public static Game getGame(String channelID) {
        return games.values().stream().filter(g -> g.getChannelID().equals(channelID)).findFirst().orElse(null);
    }

    public static void addGame(Game game) {
        games.put(game.getNumber(), game);

        Config.debug("Jogo " + game.getNumber() + " foi carregado na memoria");
    }

    public static void removeGame(Game game) {
        games.remove(game.getNumber());
    }

    public static boolean containsGame(Game game) {
        return games.containsValue(game);
    }

    public static void initializeGame(Game game) {
        if (!containsGame(game))
            addGame(game);
    }

    public static Map<Integer, Game> getGames() {
        return games;
    }
}
