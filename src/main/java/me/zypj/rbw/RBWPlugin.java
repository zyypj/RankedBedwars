package me.zypj.rbw;

import me.zypj.rbw.api.RankedBedwarsAPI;
import me.zypj.rbw.api.impl.RankedBedwarsApiImpl;
import me.zypj.rbw.commands.CommandManager;
import me.zypj.rbw.commands.moderation.UnbanTask;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.database.SQLTableManager;
import me.zypj.rbw.database.SQLite;
import me.zypj.rbw.instance.*;
import me.zypj.rbw.instance.Queue;
import me.zypj.rbw.levelsfile.Levels;
import me.zypj.rbw.listener.*;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.minecraft_commands.MCRegisterCmd;
import me.zypj.rbw.minecraft_commands.MCRenameCmd;
import me.zypj.rbw.perms.Perms;
import com.tomkeuper.bedwars.api.BedWars;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class RBWPlugin extends JavaPlugin {

    public static final String version = "2.0";
    public static BedWars bedwarsAPI = null;
    public static RBWPlugin rbwPlugin;
    public static JDA jda;
    @Getter
    public static Guild guild;

    public static RBWPlugin getInstance() {
        return rbwPlugin;
    }

    @Override
    public void onEnable() {
        rbwPlugin = this;
        bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        if (bedwarsAPI == null) {
            getServer().getConsoleSender().sendMessage("[RBW] §cBedWars2023 not found.");
            getServer().getConsoleSender().sendMessage("[RBW] §cDisabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerCommandsAndEvents();
        createDirectories();
        setupDatabase();
        loadConfigurations();
        setupDiscordBot();

        getServer().getConsoleSender().sendMessage("[RBW] \n§e[!] Finishing up... this might take around 10 seconds\n");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                initializeGuildAndLoadData();
            }
        }, 10000);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                UnbanTask.checkAndUnbanPlayers();
            }
        }, 1000 * 60 * 60, 1000 * 60 * 60);
		
		/*
		Register new api
		 */
        this.getServer().getServicesManager().register(
                RankedBedwarsAPI.class, new RankedBedwarsApiImpl(), this, org.bukkit.plugin.ServicePriority.Normal
        );
    }

    @Override
    public void onDisable() {
        SQLite.disconnect();
    }

    private void registerCommandsAndEvents() {
        getServer().getPluginManager().registerEvents(new BW2023Events(), this);
        getCommand("register").setExecutor(new MCRegisterCmd());
        getCommand("rename").setExecutor(new MCRenameCmd());
    }

    private void createDirectories() {
        List<String> directories = Arrays.asList(
                getDataFolder() + "/RankedBot/fonts",
                getDataFolder() + "/RankedBot/themes"
        );
        directories.forEach(dir -> new File(dir).mkdirs());
    }

    private void setupDatabase() {
        SQLite.connect();
        SQLTableManager.createPlayersTable();
        SQLTableManager.createRanksTable();
        SQLTableManager.createGamesTable();
        SQLTableManager.createQueuesTable();
        SQLTableManager.createClansTable();
    }

    private void loadConfigurations() {
        Config.loadConfig();
        Perms.loadPerms();
        Msg.loadMsg();
        Levels.loadLevels();
        Levels.loadClanLevels();
    }

    private void setupDiscordBot() {
        String token = Config.getValue("token");
        if (token == null) {
            getServer().getConsoleSender().sendMessage("[RBW] §e[!] Please set your token in config.yml");
            return;
        }

        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                    .setStatus(OnlineStatus.valueOf(Config.getValue("status").toUpperCase()))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new CommandManager(), new PagesEvents(), new QueueJoin(), new ServerJoin(), new PartyInviteButton())
                    .build()
                    .awaitReady();

            initializeGuildAndLoadData();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeGuildAndLoadData() {
        getServer().getConsoleSender().sendMessage("[RBW] §aGuilds detected: " + jda.getGuilds().size());

        jda.getGuilds().stream().findFirst()
                .ifPresentOrElse(
                        g -> {
                            guild = g;
                            loadThemesAndLevels();
                            loadDatabaseData();
                        },
                        () -> getServer().getConsoleSender().sendMessage("[RBW] §c[!] Please invite this bot to your server first")
                );
    }

    private void loadThemesAndLevels() {
        File themesDir = new File(getDataFolder() + "/RankedBot/themes");
        if (themesDir.exists()) {
            Arrays.stream(themesDir.listFiles())
                    .filter(f -> f.getName().endsWith(".png"))
                    .forEach(f -> new Theme(f.getName().replaceAll(".png", "")));
        }

        loadLevels(Levels.levelsData, Level::new);
        loadLevels(Levels.clanLevelsData, ClanLevel::new);
    }

    private void loadLevels(Map<String, String> levelsData, java.util.function.Consumer<Integer> consumer) {
        int totalLevels = Integer.parseInt(levelsData.get("total-levels"));
        for (int i = 0; i <= totalLevels; i++) {
            consumer.accept(i);
        }
    }

    private void loadDatabaseData() {
        Map<String, List<String>> dataMap = new HashMap<>();
        dataMap.put("ranks", new ArrayList<>());
        dataMap.put("queues", new ArrayList<>());
        dataMap.put("players", new ArrayList<>());
        dataMap.put("games", new ArrayList<>());
        dataMap.put("clans", new ArrayList<>());

        dataMap.forEach((table, list) -> {
            try (ResultSet rs = SQLite.queryData("SELECT * FROM " + table)) {
                while (rs != null && rs.next()) {
                    list.add(rs.getString(1));
                }
            } catch (SQLException e) {
                getServer().getConsoleSender().sendMessage("[RBW] §c[!] There was a problem loading data from " + table + ".");
                e.printStackTrace();
            }
        });

        loadEntities(dataMap.get("ranks"), Rank::new);
        loadArenas();
        loadEntities(dataMap.get("queues"), Queue::new);
        loadEntities(dataMap.get("players"), Player::new);
        loadEntities(dataMap.get("games"), s -> new Game(Integer.parseInt(s)));
        loadEntities(dataMap.get("clans"), Clan::new);
    }

    private void loadEntities(List<String> entities, java.util.function.Consumer<String> consumer) {
        entities.forEach(s -> {
            try {
                consumer.accept(s);
            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage("[RBW] §c[!] An entiy could not be loaded!");
            }
        });
    }

    private void loadArenas() {
        bedwarsAPI.getArenaUtil().getArenas().stream()
                .filter(arena -> arena.getGroup().equalsIgnoreCase(Config.getValue("bedwars-plugin-group")))
                .forEach(arena -> {
                    try {
                        new GameMap(arena.getArenaName());
                        getServer().getConsoleSender().sendMessage("[RBW] §a[!] " + arena.getArenaName() + " loaded!");
                    } catch (Exception e) {
                        getServer().getConsoleSender().sendMessage("[RBW] §c[!] An arena could not be loaded!");
                    }
                });
    }
}
