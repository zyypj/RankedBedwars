package com.kasp.rbw.listener;

import com.kasp.rbw.mvp.MVPManager;
import com.tomkeuper.bedwars.api.events.gameplay.GameEndEvent;
import com.tomkeuper.bedwars.api.events.gameplay.TeamAssignEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import com.tomkeuper.bedwars.api.events.server.ArenaDisableEvent;
import com.tomkeuper.bedwars.api.events.server.ArenaEnableEvent;
import com.kasp.rbw.RBW;
import com.kasp.rbw.instance.Game;
import com.kasp.rbw.instance.GameMap;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.instance.cache.MapCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BW2023Events implements Listener {

    // map, game
    public static Map<String, Integer> mapManager = new HashMap<>();

    @EventHandler
    public void teamAssignEvent(TeamAssignEvent event) {
        if (event.getArena().getGroup().equalsIgnoreCase("Ranked4s")) {
            event.setCancelled(true);

            event.getArena();
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        Game game = null;

        for (Game g : GameCache.getGames().values()) {
            if (g.isCasual()) continue;

            if (g.getNumber() == mapManager.get(event.getArena().getArenaName())) {
                game = g;
                break;
            }
        }

        if (game != null) {

            Player mvpPlayer = MVPManager.getMvpManager().determineMVP(event.getArena());

            com.kasp.rbw.instance.Player mvp = PlayerCache.getPlayerByIgn(mvpPlayer.getName());

            List<com.kasp.rbw.instance.Player> winningTeam;
            List<com.kasp.rbw.instance.Player> losingTeam;

            // check if arena winners contain the first player from game team1
            if (event.getWinners().contains(Bukkit.getPlayer(game.getTeam1().get(0).getIgn()).getUniqueId())) {
                winningTeam = game.getTeam1();
                losingTeam = game.getTeam2();
            }
            else {
                winningTeam = game.getTeam2();
                losingTeam = game.getTeam1();
            }

            game.scoreGame(winningTeam, losingTeam, mvp, RBW.guild.getMemberById(RBW.jda.getSelfUser().getId()));
        }
    }

    @EventHandler
    public void arenaEnableEvent(ArenaEnableEvent event) {
        if (!event.getArena().getGroup().equalsIgnoreCase("Ranked4s")) {
            return;
        }

        if (!MapCache.getMaps().containsKey(event.getArena().getArenaName())) {
            new GameMap(event.getArena().getArenaName());
        }
    }

    @EventHandler
    public void arenaDisableEvent(ArenaDisableEvent event) {
        if (!RBW.bedwarsAPI.getArenaUtil().getArenaByName(event.getArenaName()).getGroup().equalsIgnoreCase("Ranked4s")) {
            return;
        }

        if (MapCache.getMaps().containsKey(event.getArenaName())) {
            MapCache.removeMap(MapCache.getMap(event.getArenaName()));
        }
    }

    @EventHandler
    public void playerKillEvent(PlayerKillEvent event) {
        Player victim = event.getVictim();
        com.kasp.rbw.instance.Player rbwVictim = PlayerCache.getPlayerByIgn(victim.getName());
        if (rbwVictim != null) {
            rbwVictim.setDeaths(rbwVictim.getDeaths()+1);
        }

        if (event.getKiller() != null) {
            Player killer = event.getKiller();
            com.kasp.rbw.instance.Player rbwKiller = PlayerCache.getPlayerByIgn(killer.getName());
            if (rbwKiller != null) {
                rbwKiller.setKills(rbwKiller.getKills()+1);
            }
        }
    }
}
