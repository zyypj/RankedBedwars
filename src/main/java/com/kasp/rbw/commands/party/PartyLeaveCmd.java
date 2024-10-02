package com.kasp.rbw.commands.party;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Party;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PartyCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PartyLeaveCmd extends Command {
    public PartyLeaveCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        if (PartyCache.getParty(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(player);

        if (party.getLeader() == player) {
            Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("party-disbanded"), 1);
            msg.replyEmbeds(reply.build()).queue();

            StringBuilder mentions = new StringBuilder();
            for (Player p : party.getMembers()) {
                mentions.append("<@").append(p.getID()).append(">");
            }

            Embed embed = new Embed(EmbedType.DEFAULT, "", Msg.getMsg("your-party-disbanded"), 1);
            channel.sendMessage(mentions.toString()).setEmbeds(embed.build()).queue();

            party.disband();
        }
        else {
            player.leaveParty(party);

            Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("party-left"), 1);
            msg.replyEmbeds(reply.build()).queue();

            Embed embed = new Embed(EmbedType.DEFAULT, "", "<@" + player.getID() + "> saiu da party", 1);
            channel.sendMessage("<@" + party.getLeader().getID() + ">").setEmbeds(embed.build()).queue();
        }
    }
}
