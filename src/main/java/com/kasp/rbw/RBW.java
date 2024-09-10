package com.kasp.rbw;

import com.kasp.rbw.api.RankedBedwarsAPI;
import com.kasp.rbw.api.impl.RankedBedwarsApiImpl;
import com.kasp.rbw.commands.CommandManager;
import com.kasp.rbw.commands.moderation.UnbanTask;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.database.SQLTableManager;
import com.kasp.rbw.database.SQLite;
import com.kasp.rbw.instance.Queue;
import com.kasp.rbw.instance.*;
import com.kasp.rbw.levelsfile.Levels;
import com.kasp.rbw.listener.*;
import com.kasp.rbw.messages.Msg;
import com.kasp.rbw.minecraft_commands.MCRegisterCmd;
import com.kasp.rbw.minecraft_commands.MCRenameCmd;
import com.kasp.rbw.perms.Perms;
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

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class RBW extends JavaPlugin {
	
	public static final String version = "2.0";
	public static BedWars bedwarsAPI = null;
	public static RBW rbw;
	public static JDA jda;
	@Getter
    public static Guild guild;

    public static RBW getInstance() {
		return rbw;
	}
	
	@Override
	public void onEnable() {
		rbw = this;
		bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
		
		registerCommandsAndEvents();
		createDirectories();
		setupDatabase();
		loadConfigurations();
		setupDiscordBot();
		
		System.out.println("\n[!] Finishing up... this might take around 10 seconds\n");
		
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
			System.out.println("[!] Please set your token in config.yml");
			return;
		}
		
		try {
			jda = JDABuilder.createDefault(token)
			  .setStatus(OnlineStatus.valueOf(Config.getValue("status").toUpperCase()))
			  .setChunkingFilter(ChunkingFilter.ALL)
			  .setMemberCachePolicy(MemberCachePolicy.ALL)
			  .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
			  .addEventListeners(new CommandManager(), new PagesEvents(), new QueueJoin(), new ServerJoin(), new PartyInviteButton())
			  .build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeGuildAndLoadData() {
		jda.getGuilds().stream().findFirst()
		  .ifPresentOrElse(
			g -> {
				guild = g;
				loadThemesAndLevels();
				loadDatabaseData();
			},
			() -> System.out.println("[!] Please invite this bot to your server first")
		  );
		
		System.out.println("-------------------------------");
		System.out.println("RankedBedwars has been successfully enabled!");
		System.out.println("NOTE: this bot can only be in 1 server, otherwise it'll break");
		System.out.println("Don't forget to configure config.yml and permissions.yml before using it.\nYou can also edit messages.yml (optional).");
		System.out.println("IMPORTANT: if you add/remove maps in-game, please restart the bot");
		System.out.println("-------------------------------");
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
				System.out.println("[!] There was a problem loading data from " + table + ".");
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
				System.out.println("[!] An entity could not be loaded!");
			}
		});
	}
	
	private void loadArenas() {
		bedwarsAPI.getArenaUtil().getArenas().stream()
		  .filter(arena -> arena.getGroup().equalsIgnoreCase("Ranked4s"))
		  .forEach(arena -> {
			  try {
				  new GameMap(arena.getArenaName());
			  } catch (Exception e) {
				  System.out.println("[!] An arena could not be loaded!");
			  }
		  });
	}
}
