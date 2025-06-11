package me.zypj.rbw.instance;

import lombok.Getter;
import lombok.Setter;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.cache.PartyCache;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Party {

    private Player leader;
    private List<Player> members;
    private List<Player> invitedPlayers;

    public Party(Player leader) {
        this.leader = leader;
        members = new ArrayList<>();
        invitedPlayers = new ArrayList<>();
        members.add(leader);

        PartyCache.initializeParty(this);
    }

    public void invite(Player invited) {

        invitedPlayers.add(invited);

        Bukkit.getScheduler().runTaskLater(RBWPlugin.getInstance(), () -> invitedPlayers.remove(invited), Integer.parseInt(Config.getValue("invite-expiration")) * 60L * 20L);
    }

    public void disband() {
        PartyCache.removeParty(this);
    }

    public void promote(Player player) {
        leader = player;
    }

}
