package me.zypj.rbw.commands.clan;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Clan;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.ClanCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ClanInfoCmd extends Command {
    public ClanInfoCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String clanName;
        if (args.length == 1)
            if (ClanCache.getClan(PlayerCache.getPlayer(sender.getId())) != null) {
                clanName = ClanCache.getClan(PlayerCache.getPlayer(sender.getId())).getName();
            }
            else {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-clan"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }
        else {
            clanName = args[1];
        }

        if (ClanCache.getClan(clanName) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Clan", Msg.getMsg("clan-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(clanName);

        StringBuilder members = new StringBuilder();
        for (Player p : clan.getMembers()) {
            members.append("<@").append(p.getID()).append("> ");
        }

        StringBuilder invited = new StringBuilder();
        for (Player p : clan.getInvitedPlayers()) {
            invited.append("<@").append(p.getID()).append("> ");
        }

        String eloReq = "";
        if (!clan.isPrivate()) {
            eloReq = " - `" + clan.getEloJoinReq() + "` ELO required to enter";
        }

        Embed embed = new Embed(EmbedType.DEFAULT, clan.getName() + " Clan Info", "- Disable stats with `=cstats`", 1);
        embed.addField("Private", clan.isPrivate() + eloReq, false);
        embed.addField("Leader", "<@" + clan.getLeader().getID() + ">", false);
        embed.addField("Members `[" + clan.getMembers().size() + "/" + Config.getValue("l" + clan.getLevel().getLevel()) + "]`", members.toString(), false);
        if (!clan.getInvitedPlayers().isEmpty()) {
            embed.addField("Guest Players `[" + clan.getInvitedPlayers().size() + "]`", invited.toString(), false);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
