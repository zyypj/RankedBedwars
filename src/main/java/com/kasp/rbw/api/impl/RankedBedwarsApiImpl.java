package com.kasp.rbw.api.impl;

import com.kasp.rbw.RBW;
import com.kasp.rbw.api.RankedBedwarsAPI;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankedBedwarsApiImpl implements RankedBedwarsAPI {
	
	@Override
	public int getElo(String playerName) {
		com.kasp.rbw.instance.Player player = getPlayerByName(playerName);
		return player != null ? player.getElo() : 0;
	}
	
	@Override
	public int getElo(Player player) {
		return getElo(player.getName());
	}
	
	@Override
	public void setElo(String playerName, int elo) {
		com.kasp.rbw.instance.Player player = getPlayerByName(playerName);
		player.setElo(elo);
		
		PlayerCache.removePlayer(player);
		PlayerCache.addPlayer(player);
	}
	
	@Override
	public void setElo(Player player, int elo) {
		setElo(player.getName(), elo);
	}
	
	@Override
	public com.kasp.rbw.instance.Player getPlayerByName(String playerName) {
		return PlayerCache.getPlayerByIgn(playerName);
	}
	
	@Override
	public void finishGameOf(com.kasp.rbw.instance.Player player, int closeChannelDelay) {
		GameCache.getGames().values().stream()
		  .filter(game -> game.getPlayers().contains(player))
		  .forEach(game -> game.closeChannel(closeChannelDelay));
		
		Player bukkitPlayer = Bukkit.getPlayer(player.getIgn());
		if (bukkitPlayer != null) {
			IArena arena = RBW.bedwarsAPI.getArenaUtil().getArenaByPlayer(bukkitPlayer);
			if (arena != null) {
				arena.getPlayers().forEach(arena::abandonGame);
			}
		}
	}
	
}
