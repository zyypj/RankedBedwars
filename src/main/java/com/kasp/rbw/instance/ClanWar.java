package com.kasp.rbw.instance;

import com.kasp.rbw.config.Config;
import com.kasp.rbw.database.SQLClanWars;

import java.util.List;

public class ClanWar {

    private String warId;
    private int playersPerTeam;
    private int minClans;
    private int maxClans;
    private int xpPerWin;
    private int goldPerWin;
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
            System.out.println("Clan " + clanName + " registered for war ID: " + warId);
        } else {
            System.out.println("War is full. Cannot register more clans.");
        }
    }

    public void unregisterClan(String clanName) {
        SQLClanWars.unregisterClan(warId, clanName);
        System.out.println("Clan " + clanName + " unregistered from war ID: " + warId);
    }

    public List<String> getRegisteredClans() {
        return SQLClanWars.getRegisteredClans(warId);
    }

    // Getters and Setters
    public String getWarId() {
        return warId;
    }

    public int getPlayersPerTeam() {
        return playersPerTeam;
    }

    public int getMinClans() {
        return minClans;
    }

    public int getMaxClans() {
        return maxClans;
    }

    public int getXpPerWin() {
        return xpPerWin;
    }

    public int getGoldPerWin() {
        return goldPerWin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}