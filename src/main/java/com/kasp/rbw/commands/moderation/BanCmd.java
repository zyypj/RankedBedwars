package com.kasp.rbw.commands.moderation;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.LocalDateTime;
import java.util.Objects;

public class BanCmd extends Command {
    public BanCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 4) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");
        String duration = args[2];
        String reason = msg.getContentRaw().replaceAll(args[0], "").replaceAll(args[1], "").replaceAll(args[2], "").trim();

        Player player = PlayerCache.getPlayer(ID);

        LocalDateTime unban;

        if (duration.contains("m")) {
            unban = LocalDateTime.now().plusMinutes(Integer.parseInt(duration.replaceAll("[^0-9]", "")));
        } else if (duration.contains("h")) {
            unban = LocalDateTime.now().plusHours(Integer.parseInt(duration.replaceAll("[^0-9]", "")));
        } else if (duration.contains("d")) {
            unban = LocalDateTime.now().plusDays(Integer.parseInt(duration.replaceAll("[^0-9]", "")));
        } else {
            Embed error = new Embed(EmbedType.ERROR, "", Msg.getMsg("incorrect-time-format"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (player.ban(unban, reason)) {
            Embed embed = new Embed(EmbedType.DEFAULT, "`" + player.getIgn() + " foi banido`", "Use `=fix` quando o tempo de punição acabar para retirar o banimento\nSe você acha que foi um banimento errado, abra um ticket", 1);
            embed.addField("Duração", duration, false);
            embed.addField("Motivo", reason, false);

            if (!Objects.equals(Config.getValue("ban-channel"), null)) {
                Objects.requireNonNull(guild.getTextChannelById(Config.getValue("ban-channel"))).sendMessageEmbeds(embed.build()).queue();
            }

            Embed success = new Embed(EmbedType.SUCCESS, "", "Você foi banido <@!" + ID + "> por `" + duration + "`\n**Motivo:** " + reason, 1);
            msg.replyEmbeds(success.build()).queue();
        } else {
            Embed error = new Embed(EmbedType.ERROR, "", "Esse jogador já está banido\nMotivo: `" + player.getBanReason() + "`", 1);
            msg.replyEmbeds(error.build()).queue();
        }
    }
}
