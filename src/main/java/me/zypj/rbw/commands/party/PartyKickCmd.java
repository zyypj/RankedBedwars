package me.zypj.rbw.commands.party;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
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

public class PartyKickCmd extends Command {
    public PartyKickCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        if (PartyCache.getParty(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(player);

        if (party.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-party-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        if (PlayerCache.getPlayer(ID) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Player", Msg.getMsg("invalid-player"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player kicked = PlayerCache.getPlayer(ID);

        if (!party.getMembers().contains(kicked)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("player-not-in-ur-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        kicked.leaveParty(party);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("player-kicked-party"), 1);
        msg.replyEmbeds(reply.build()).queue();

        Embed embed = new Embed(EmbedType.DEFAULT, "", Msg.getMsg("youre-kicked-party"), 1);
        channel.sendMessage("<@" + kicked.getID() + ">").setEmbeds(embed.build()).queue();
    }
}
