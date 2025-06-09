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

public class ClanJoinCmd extends Command {
    public ClanJoinCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (ClanCache.getClan(player) != null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-in-clan"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (!ClanCache.containsClan(args[1])) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(args[1]);

        if (!clan.isPrivate()) {
            if (clan.getMembers().size() >= Integer.parseInt(Config.getValue("l" + clan.getLevel().getLevel()))) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-max-players"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            if (player.getElo() < clan.getEloJoinReq()) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-enough-elo-join"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }
        }
        else {
            if (!clan.getInvitedPlayers().contains(player)) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-not-invited"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            if (clan.getMembers().size() >= Integer.parseInt(Config.getValue("l" + clan.getLevel().getLevel()))) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-max-players"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            clan.getInvitedPlayers().remove(player);
        }

        clan.getMembers().add(player);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("clan-joined"), 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
