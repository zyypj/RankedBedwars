package me.zypj.rbw.instance.cache;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Party;
import me.zypj.rbw.instance.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyCache {
	
	private static final List<Party> parties = new ArrayList<>();
	
	public static Party getParty(Player player) {
		return parties.stream().filter(party -> party.getMembers().contains(player)).findFirst().orElse(null);
	}
	
	public static void addParty(Party party) {
		parties.add(party);
		
		Config.debug("Party created by " + party.getLeader().getIgn() + " foi carregado na memoria");
	}
	
	public static void removeParty(Party party) {
		parties.remove(party);
	}
	
	public static boolean containsParty(Party party) {
		return parties.contains(party);
	}
	
	public static void initializeParty(Party party) {
		if (!containsParty(party))
			addParty(party);
	}
	
	public static List<Party> getParties() {
		return parties;
	}
}
