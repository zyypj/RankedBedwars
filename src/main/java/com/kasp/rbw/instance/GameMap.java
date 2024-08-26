package com.kasp.rbw.instance;

import com.kasp.rbw.RBW;
import com.kasp.rbw.instance.cache.MapCache;
import com.tomkeuper.bedwars.api.arena.GameState;

import java.util.Timer;
import java.util.TimerTask;

public class GameMap {

    private String name;
    private int height;
    private String team1;
    private String team2;
    private GameState arenaState;
    private int maxPlayers;

    public GameMap(String name) {
        this.name = name;

        this.height = RBW.bedwarsAPI.getArenaUtil().getArenaByName(name).getConfig().getInt("max-build-y");
        this.team1 = RBW.bedwarsAPI.getArenaUtil().getArenaByName(name).getTeams().get(0).getName();
        this.team2 = RBW.bedwarsAPI.getArenaUtil().getArenaByName(name).getTeams().get(1).getName();
        this.arenaState = RBW.bedwarsAPI.getArenaUtil().getArenaByName(name).getStatus();
        this.maxPlayers = RBW.bedwarsAPI.getArenaUtil().getArenaByName(name).getMaxInTeam();

        MapCache.initializeMap(name, this);

        TimerTask checkTask = new TimerTask () {
            @Override
            public void run () {
                if (RBW.bedwarsAPI.getArenaUtil().getArenaByName(name) == null) {
                    cancel();
                    if (MapCache.containsMap(name))
                        MapCache.removeMap(MapCache.getMap(name));
                    return;
                }

                MapCache.getMap(name).setArenaState(RBW.bedwarsAPI.getArenaUtil().getArenaByName(name).getStatus());
            }
        };

        new Timer().schedule(checkTask, 1000, 1000);
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
