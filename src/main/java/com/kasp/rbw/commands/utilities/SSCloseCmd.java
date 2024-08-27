package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.RBW;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.ScreenShare;
import com.kasp.rbw.instance.cache.ScreenshareCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Timer;
import java.util.TimerTask;

public class SSCloseCmd extends Command {
    public SSCloseCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (ScreenshareCache.getScreenshare(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", "Use esse comando no chat de SS", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        ScreenShare ss = ScreenshareCache.getScreenshare(channel.getId());
        String reason = msg.getContentRaw().replaceAll(args[0], "").trim();

        Embed embed = new Embed(EmbedType.SUCCESS, "Screenshare de " + ss.getTarget().getIgn() + " fechado", "", 1);
        embed.setDescription("Motivo: `" + reason + "`\n\nCanal sendo deletado em `3` mins");

        msg.reply("<@" + ss.getTarget().getID() + ">").setEmbeds(embed.build()).queue();

        TimerTask closingTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (guild.getTextChannelById(ss.getChannelID()) != null) {
                        guild.getTextChannelById(ss.getChannelID()).delete().queue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Timer().schedule(closingTask, 180 * 1000L);

        embed.setType(EmbedType.DEFAULT);
        embed.setTitle(ss.getTarget().getIgn() + " (" + ss.getTarget().getID() + ")");
        embed.setDescription("Alvo: <@" + ss.getTarget().getID() + ">\n" +
                "Pedido por: <@" + ss.getRequestedBy().getID() + ">\n" +
                "Screenshared por: <@" + sender.getId() + ">\n\n" +
                "Motivo do fechamento: `" + reason + "`");

        RBW.guild.getTextChannelById(Config.getValue("ss-log-channel")).sendMessageEmbeds(embed.build()).queue();
    }
}
