package com.kasp.rbw.commands.clan;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Clan;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.ClanCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
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
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String clanName;
        if (args.length == 1)
            if (ClanCache.getClan(PlayerCache.getPlayer(sender.getId())) != null) {
                clanName = ClanCache.getClan(PlayerCache.getPlayer(sender.getId())).getName();
            }
            else {
                Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-in-clan"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }
        else {
            clanName = args[1];
        }

        if (ClanCache.getClan(clanName) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Clan Inválido", Msg.getMsg("clan-doesnt-exist"), 1);
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
            eloReq = " - `" + clan.getEloJoinReq() + "` ELO requerido para entrar";
        }

        Embed embed = new Embed(EmbedType.DEFAULT, clan.getName() + " Clan Info", "- Desative os stats com `=cstats`", 1);
        embed.addField("Privado", clan.isPrivate() + eloReq, false);
        embed.addField("Líder", "<@" + clan.getLeader().getID() + ">", false);
        embed.addField("Membros `[" + clan.getMembers().size() + "/" + Config.getValue("l" + clan.getLevel().getLevel()) + "]`", members.toString(), false);
        if (!clan.getInvitedPlayers().isEmpty()) {
            embed.addField("Jogadores Convidados `[" + clan.getInvitedPlayers().size() + "]`", invited.toString(), false);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
