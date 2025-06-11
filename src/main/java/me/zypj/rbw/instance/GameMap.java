package me.zypj.rbw.instance;

import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.instance.cache.MapCache;
import com.tomkeuper.bedwars.api.arena.GameState;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameMap {

    private String name;
    private int height;
    private String team1;
    private String team2;
    private GameState arenaState;
    private int maxPlayers;

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

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public String getTeam1() {
        return team1;
    }
    public void setTeam1(String team1) {
        this.team1 = team1;
    }
    public String getTeam2() {
        return team2;
    }
    public void setTeam2(String team2) {
        this.team2 = team2;
    }
    public GameState getArenaState() {
        return arenaState;
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setArenaState(GameState arenaState) {
        this.arenaState = arenaState;
    }
}
