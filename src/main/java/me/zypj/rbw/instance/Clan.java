package me.zypj.rbw.instance;

import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.database.SQLClanManager;
import me.zypj.rbw.database.SQLite;
import me.zypj.rbw.instance.cache.ClanCache;
import me.zypj.rbw.instance.cache.ClanLevelCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Clan {

    // DATA
    private final String name;
    private final Player leader;
    private final List<Player> members;
    private int reputation;
    private int xp;
    private ClanLevel level;

    // CLAN WAR
    private int wins;
    private int losses;

    // SETTINGS
    private boolean isPrivate;
    private int eloJoinReq;
    private String description;

    private final List<Player> invitedPlayers;

    // CREATE CLAN
    public Clan(String name, Player leader) {
        members = new ArrayList<>();
        invitedPlayers = new ArrayList<>();
        members.add(leader);

        this.name = name;
        this.leader = leader;
        reputation = Integer.parseInt(Config.getValue("clan-starting-rep"));
        level = ClanLevelCache.getLevel(0);

        this.isPrivate = true;
        this.description = "Um novo clan";
        ClanCache.initializeClan(name, this);
        create();
    }

    // LOAD CLAN
    public Clan(String name) {
        invitedPlayers = new ArrayList<>();
        this.name = name;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM clans WHERE name='" + name + "';");

        try {
            this.leader = PlayerCache.getPlayer(resultSet.getString(2));
            this.members = new ArrayList<>();
            for (String ID : resultSet.getString(3).split(",")) {
                members.add(PlayerCache.getPlayer(ID));
            }
            this.reputation = resultSet.getInt(4);
            this.xp = resultSet.getInt(5);
            this.level = ClanLevelCache.getLevel(resultSet.getInt(6));
            this.wins = resultSet.getInt(7);
            this.losses = resultSet.getInt(8);
            this.isPrivate = Boolean.parseBoolean(resultSet.getString(9));
            this.eloJoinReq = resultSet.getInt(10);
            this.description = resultSet.getString(11);
            
            SQLite.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ClanCache.initializeClan(name, this);
    }

    public void disband() {
        ClanCache.removeClan(name);

        try {
            Files.deleteIfExists(Path.of(RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clans/" + name + "/theme.png"));
            Files.deleteIfExists(Path.of(RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clans/" + name + "/icon.png"));
            Files.deleteIfExists(Path.of(RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clans/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create() {

        new File(RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clans/" + name).mkdirs();

        if (!new File(RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clans/" + name + "/data.yml").exists()) {
            description = "A newly created RBW clan";
        }

        SQLClanManager.createClan(name, leader.getID());
    }

    public static void deleteIcon(String name) {
        try {
            Files.deleteIfExists(Path.of(RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clans/" + name + "/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTheme(String name) {
        try {
            Files.deleteIfExists(Path.of(RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clans/" + name + "/theme.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addReputation(int number) {
        this.reputation = getReputation() + number;
        SQLClanManager.updateReputation(name);
    }

    public void removeReputation(int number) {
        this.reputation = getReputation() - number;
        if (this.reputation < 0) {
            this.reputation = 0;
        }
        SQLClanManager.updateReputation(name);
    }

    public void setReputation(int number) {
        this.reputation = number;
        SQLClanManager.updateReputation(name);
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
        SQLClanManager.updatePrivate(name);
    }

    public void setEloJoinReq(int eloJoinReq) {
        this.eloJoinReq = eloJoinReq;
        SQLClanManager.updateEloJoinReq(name);
    }

    public void setDescription(String description) {
        this.description = description;
        SQLClanManager.updateDescription(name);
    }

    public void setXp(int xp) {
        this.xp = xp;
        SQLClanManager.updateXP(name);
    }

    public void setLevel(ClanLevel level) {
        this.level = level;
        SQLClanManager.updateLevel(name);
    }
}
