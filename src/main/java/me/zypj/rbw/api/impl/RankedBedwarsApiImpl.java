package me.zypj.rbw.api.impl;

import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.api.RankedBedwarsAPI;
import me.zypj.rbw.instance.cache.GameCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankedBedwarsApiImpl implements RankedBedwarsAPI {
	
	@Override
	public int getElo(String playerName) {
		me.zypj.rbw.instance.Player player = getPlayerByName(playerName);
		return player != null ? player.getElo() : 0;
	}
	
	@Override
	public int getElo(Player player) {
		return getElo(player.getName());
	}
	
	@Override
	public void setElo(String playerName, int elo) {
		me.zypj.rbw.instance.Player player = getPlayerByName(playerName);
		player.setElo(elo);
		
		PlayerCache.removePlayer(player);
		PlayerCache.addPlayer(player);
	}
	
	@Override
	public void setElo(Player player, int elo) {
		setElo(player.getName(), elo);
	}
	
	@Override
	public me.zypj.rbw.instance.Player getPlayerByName(String playerName) {
		return PlayerCache.getPlayerByIgn(playerName);
	}
	
	@Override
	public void finishGameOf(Player gamer, int closeChannelDelay) {
		me.zypj.rbw.instance.Player player = getPlayerByName(gamer.getName());
		GameCache.getGames().values().stream()
		  .filter(game -> game.getPlayers().contains(player))
		  .forEach(game -> game.closeChannel(closeChannelDelay));
		
		Player bukkitPlayer = Bukkit.getPlayer(player.getIgn());
		if (bukkitPlayer != null) {
			IArena arena = RBWPlugin.bedwarsAPI.getArenaUtil().getArenaByPlayer(bukkitPlayer);
			if (arena != null) {
				arena.getPlayers().forEach(arena::abandonGame);
			}
		}
	}
}