package me.zypj.rbw.instance;

import lombok.Getter;
import lombok.Setter;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.instance.cache.MapCache;
import com.tomkeuper.bedwars.api.arena.GameState;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class GameMap {

    private String name;
    private int height;
    private String team1;
    private String team2;
    private GameState arenaState;
    private final int maxPlayers;

    public GameMap(String name) {
        this.name = name;

        this.height = RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByName(name).getConfig().getInt("max-build-y");
        this.team1 = RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByName(name).getTeams().get(0).getName();
        this.team2 = RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByName(name).getTeams().get(1).getName();
        this.arenaState = RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByName(name).getStatus();
        this.maxPlayers = RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByName(name).getMaxInTeam();

        MapCache.initializeMap(name, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByName(name) == null) {
                    cancel();
                    if (MapCache.containsMap(name))
                        MapCache.removeMap(MapCache.getMap(name));
                    return;
                }

                MapCache.getMap(name).setArenaState(RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByName(name).getStatus());
            }
        }.runTaskTimer(RBWPlugin.getInstance(), 20L, 20L);
    }
}
