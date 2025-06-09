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

public class PartyLeaveCmd extends Command {
    public PartyLeaveCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
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

            Embed embed = new Embed(EmbedType.DEFAULT, "", "<@" + player.getID() + "> left the party", 1);
            channel.sendMessage("<@" + party.getLeader().getID() + ">").setEmbeds(embed.build()).queue();
        }
    }
}
