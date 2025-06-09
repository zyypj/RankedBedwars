package me.zypj.rbw.api;

import org.bukkit.entity.Player;

public interface RankedBedwarsAPI {
	
	int getElo(String playerName);
	
	int getElo(Player player);
	
	void setElo(String playerName, int elo);
	
	void setElo(Player player, int elo);
	
	me.zypj.rbw.instance.Player getPlayerByName(String playerName);
	
	void finishGameOf(Player player, int closeChannelDelay);
}
