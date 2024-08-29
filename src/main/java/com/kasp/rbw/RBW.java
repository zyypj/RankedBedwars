package com.kasp.rbw;

import com.kasp.rbw.commands.CommandManager;
import com.kasp.rbw.commands.moderation.UnbanTask;
import com.kasp.rbw.commandsMC.MCRegisterCmd;
import com.kasp.rbw.commandsMC.MCRenameCmd;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.database.SQLTableManager;
import com.kasp.rbw.database.SQLite;
import com.kasp.rbw.instance.*;
import com.kasp.rbw.levelsfile.Levels;
import com.kasp.rbw.listener.*;
import com.kasp.rbw.messages.Msg;
import com.kasp.rbw.perms.Perms;
import com.tomkeuper.bedwars.api.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public final class RBW extends JavaPlugin {

    public static BedWars bedwarsAPI = null;

    private static RBW rbw;

    public static JDA jda;

    public static String version = "2.0";
    public static Guild guild;

    @Override
    public void onEnable() {
        rbw = this;

        bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        getServer().getPluginManager().registerEvents(new BW2023Events(), this);
        getCommand("register").setExecutor(new MCRegisterCmd());
        getCommand("rename").setExecutor(new MCRenameCmd());

        new File(getDataFolder() + "/RankedBot/fonts").mkdirs();
        new File(getDataFolder() + "/RankedBot/themes").mkdirs();

        SQLite.connect();
        SQLTableManager.createPlayersTable();
        SQLTableManager.createRanksTable();
        SQLTableManager.createGamesTable();
        SQLTableManager.createQueuesTable();
        SQLTableManager.createClansTable();

        Config.loadConfig();
        Perms.loadPerms();
        Msg.loadMsg();
        Levels.loadLevels();
        Levels.loadClanLevels();

        if (Config.getValue("token") == null) {
            System.out.println("[!] Please set your token in config.yml");
            return;
        }

        JDABuilder jdaBuilder = JDABuilder.createDefault(Config.getValue("token"));
        jdaBuilder.setStatus(OnlineStatus.valueOf(Config.getValue("status").toUpperCase()));
        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES);
        jdaBuilder.addEventListeners(new CommandManager(), new PagesEvents(), new QueueJoin(), new ServerJoin(), new PartyInviteButton());
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        System.out.println("\n[!] Finishing up... this might take around 10 seconds\n");

        // get guild
        TimerTask task = new TimerTask () {
            @Override
            public void run () {
                if (jda.getGuilds().size() == 0) {
                    System.out.println("[!] Please invite this bot to your server first");
                    return;
                }

                guild = jda.getGuilds().get(0);

                // =============
                // LOAD ALL THEMES AND LEVELS
                // =============

                if (new File(getDataFolder() + "/RankedBot/themes").listFiles().length > 0) {
                    for (File f : new File(getDataFolder() + "/RankedBot/themes").listFiles()) {
                        new Theme(f.getName().replaceAll(".png", ""));
                    }
                }

                for (int i = 0; i <= Integer.parseInt(Levels.levelsData.get("total-levels")); i++) {
                    new Level(i);
                }

                for (int i = 0; i <= Integer.parseInt(Levels.clanLevelsData.get("total-levels")); i++) {
                    new ClanLevel(i);
                }

                // =============
                // LOAD ALL DATA FROM DB
                // =============

                List<String> ranks = new ArrayList<>();
                //List<String> maps = new ArrayList<>();
                List<String> queues = new ArrayList<>();
                List<String> players = new ArrayList<>();
                List<String> games = new ArrayList<>();
                List<String> clans = new ArrayList<>();

                try {
                    ResultSet rs = SQLite.queryData("SELECT * FROM ranks");
                    while (rs.next()) {
                        ranks.add(rs.getString(1));
                    }

                    /*rs = SQLite.queryData("SELECT * FROM maps");
                    while (rs.next()) {
                        maps.add(rs.getString(1));
                    }*/

                    rs = SQLite.queryData("SELECT * FROM queues");
                    while (rs.next()) {
                        queues.add(rs.getString(1));
                    }

                    rs = SQLite.queryData("SELECT * FROM players");
                    while (rs.next()) {
                        players.add(rs.getString(1));
                    }

                    rs = SQLite.queryData("SELECT * FROM games");
                    while (rs.next()) {
                        games.add(rs.getString(1));
                    }

                    rs = SQLite.queryData("SELECT * FROM clans");
                    while (rs.next()) {
                        clans.add(rs.getString(1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("[!] There was a problem loading all data.");
                }

                for (String s : ranks) {
                    try {
                        new Rank(s);
                    } catch (Exception e) {
                        System.out.println("[!] a rank could not be loaded!");
                    }
                }

                /*for (String s : maps) {
                    try {
                        new GameMap(s);
                    } catch (Exception e) {
                        System.out.println("[!] a map could not be loaded!");
                    }
                }*/

                for (IArena arena : bedwarsAPI.getArenaUtil().getArenas()) {
                    if (arena.getGroup().equalsIgnoreCase("Ranked4s")) {
                        try {
                            new GameMap(arena.getArenaName());
                        } catch (Exception e) {
                            System.out.println("[!] a map could not be loaded!");
                        }
                    }
                }

                for (String s : queues) {
                    try {
                        new Queue(s);
                    } catch (Exception e) {
                        System.out.println("[!] a queue could not be loaded!");
                    }
                }

                for (String s : players) {
                    try {
                        new Player(s);
                    } catch (Exception e) {
                        System.out.println("[!] a queue could not be loaded!");
                    }
                }

                for (String s : games) {
                    try {
                        new Game(Integer.parseInt(s));
                    } catch (Exception e) {
                        System.out.println("[!] a game could not be loaded!");
                    }
                }

                for (String s : clans) {
                    try {
                        new Clan(s);
                    } catch (Exception e) {
                        System.out.println("[!] a clan could not be loaded!");
                    }
                }

                System.out.println("-------------------------------");

                System.out.println("RankedBedwars has been successfully enabled!");
                System.out.println("NOTE: this bot can only be in 1 server, otherwise it'll break");
                System.out.println("don't forget to configure config.yml and permissions.yml before using it\nYou can also edit messages.yml (optional)");
                System.out.println(" ");
                System.out.println("IMPORTANT: if you add/remove maps in-game please restart the bot");

                System.out.println("-------------------------------");
            }
        };

        new Timer().schedule(task, 10000);

        TimerTask unbanTask = new TimerTask () {
            @Override
            public void run () {
                UnbanTask.checkAndUnbanPlayers();
            }
        };

        new Timer().schedule(unbanTask, 1000 * 60 * 60, 1000 * 60 * 60);
    }

    public static Guild getGuild() {
        return guild;
    }

    public static RBW getInstance() { return rbw; }
}
