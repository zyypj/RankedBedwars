package me.zypj.rbw.commands.party;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Party;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PartyCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

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
        StringBuilder players = new StringBuilder();

        for (Player p : party.getMembers()) {
            players.append("<@").append(p.getID()).append("> ");
        }

        StringBuilder invited = new StringBuilder();

        for (Player p : party.getInvitedPlayers()) {
            invited.append("<@").append(p.getID()).append("> ");
        }

        Embed embed = new Embed(EmbedType.DEFAULT, party.getLeader().getIgn() + " party info", "", 1);
        embed.addField(title, players.toString(), false);

        if (!invited.toString().isEmpty()) {
            embed.addField("Invited", invited.toString(), false);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
