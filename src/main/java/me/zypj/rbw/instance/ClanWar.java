package me.zypj.rbw.instance;

import lombok.Getter;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.database.SQLClanWars;
import org.bukkit.Bukkit;

import java.util.List;

@Getter
public class ClanWar {

    // Getters and Setters
    private final String warId;
    private final int playersPerTeam;
    private final int minClans;
    private final int maxClans;
    private final int xpPerWin;
    private final int goldPerWin;
    private boolean isActive;

    public ClanWar(String warId, int playersPerTeam, int minClans, int maxClans, int xpPerWin, int goldPerWin, boolean isActive) {
        this.warId = warId;
        this.playersPerTeam = playersPerTeam;
        this.minClans = minClans;
        this.maxClans = maxClans;
        this.xpPerWin = xpPerWin;
        this.goldPerWin = goldPerWin;
        this.isActive = isActive;
    }

    public void startWar() {
        if (isActive && getRegisteredClans().size() >= minClans) {
            // later...
            Config.debug("Clan War started with ID: " + warId);
        } else {
            Config.debug("Não há clans registrados o suficiente para iniciar esta clan war");
        }
    }

    public void endWar() {
        if (isActive) {
            // later...
            this.isActive = false;
            SQLClanWars.updateWarStatus(warId, false);
            Config.debug("Clan War com o ID: " + warId + " terminou.");
        }
    }

    public void registerClan(String clanName) {
        if (getRegisteredClans().size() < maxClans) {
            SQLClanWars.registerClan(warId, clanName);
            Bukkit.getServer().getConsoleSender().sendMessage("[RBW] Clan " + clanName + " registered for war ID: " + warId);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage("[RBW] §cWar is full. Cannot register more clans.");
        }
    }

    public void unregisterClan(String clanName) {
        SQLClanWars.unregisterClan(warId, clanName);
        Bukkit.getServer().getConsoleSender().sendMessage("[RBW] Clan " + clanName + " unregistered for war ID: " + warId);
    }

    public List<String> getRegisteredClans() {
        return SQLClanWars.getRegisteredClans(warId);
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}