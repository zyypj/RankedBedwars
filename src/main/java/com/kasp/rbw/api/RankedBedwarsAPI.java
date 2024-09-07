package com.kasp.rbw.api;

import org.bukkit.entity.Player;

public interface RankedBedwarsAPI {
	
	public int getElo(String playerName);
	
	public int getElo(Player player);
	
	public void setElo(String playerName, int elo);
	
	public void setElo(Player player, int elo);
	
	public com.kasp.rbw.instance.Player getPlayerByName(String playerName);
	
	public void finishGameOf(com.kasp.rbw.instance.Player player, int closeChannelDelay);
}
