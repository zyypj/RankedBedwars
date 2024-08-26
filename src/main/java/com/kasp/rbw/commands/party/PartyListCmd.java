package com.kasp.rbw.commands.party;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Party;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PartyCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PartyListCmd extends Command {
    public PartyListCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID;

        if (args.length == 2) {
            ID = args[1].replaceAll("[^0-9]", "");
        }
        else {
            ID = sender.getId();
        }

        if (PartyCache.getParty(PlayerCache.getPlayer(ID)) == null) {
            Embed reply;
            if (args.length == 1) {
                reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-party"), 1);
            }
            else {
                reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("player-not-in-party"), 1);
            }

            msg.replyEmbeds(reply.build()).queue();
            return;

        }

        Party party = PartyCache.getParty(PlayerCache.getPlayer(ID));

        String title = "players `[" + party.getMembers().size() + "/" + Config.getValue("max-party-members") + "]`";
        String players = "";

        for (Player p : party.getMembers()) {
            players += "<@" + p.getID() + "> ";
        }

        String invited = "";

        for (Player p : party.getInvitedPlayers()) {
            invited += "<@" + p.getID() + "> ";
        }

        Embed embed = new Embed(EmbedType.DEFAULT, party.getLeader().getIgn() + " party info", "", 1);
        embed.addField(title, players, false);

        if (!invited.equals("")) {
            embed.addField("Invited", invited, false);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
