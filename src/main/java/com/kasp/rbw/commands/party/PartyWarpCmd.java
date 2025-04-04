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

import java.util.Objects;

public class PartyWarpCmd extends Command {
    public PartyWarpCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (party.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-party-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        StringBuilder warped = new StringBuilder();
        for (Player p : party.getMembers()) {
            if (!p.getID().equals(sender.getId())) {
                try {
                    guild.moveVoiceMember(Objects.requireNonNull(guild.getMemberById(p.getID())), Objects.requireNonNull(sender.getVoiceState()).getChannel()).queue();
                    warped.append("<@").append(p.getID()).append("> ");
                } catch (Exception ignored) {}
            }
        }

        Embed embed;
        if (warped.toString().isEmpty()) {
            embed = new Embed(EmbedType.ERROR, "", Msg.getMsg("couldnt-warp"), 1);
        }
        else {
            embed = new Embed(EmbedType.SUCCESS, "", "Você puxou todos os jogadores da sua party para você", 1);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
