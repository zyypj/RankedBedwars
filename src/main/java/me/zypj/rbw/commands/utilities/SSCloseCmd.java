package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.ScreenShare;
import me.zypj.rbw.instance.cache.ScreenshareCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;
import org.bukkit.Bukkit;

public class SSCloseCmd extends Command {
    public SSCloseCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (ScreenshareCache.getScreenshare(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "Use esse comando no chat de SS", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        ScreenShare ss = ScreenshareCache.getScreenshare(channel.getId());
        String reason = msg.getContentRaw().replaceAll(args[0], "").trim();

        Embed embed = new Embed(EmbedType.SUCCESS, "Screenshare de " + ss.getTarget().getIgn() + " fechado", "", 1);
        embed.setDescription("Reason: `" + reason + "`\n\nCanal sendo deletado em `3` mins");

        msg.reply("<@" + ss.getTarget().getID() + ">").setEmbeds(embed.build()).queue();

        Bukkit.getScheduler().runTaskLater(RBWPlugin.getInstance(), () -> {
            try {
                if (guild.getTextChannelById(ss.getChannelID()) != null) {
                    Objects.requireNonNull(guild.getTextChannelById(ss.getChannelID())).delete().queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20L * 60 * 3);

        embed.setType(EmbedType.DEFAULT);
        embed.setTitle(ss.getTarget().getIgn() + " (" + ss.getTarget().getID() + ")");
        embed.setDescription("Alvo: <@" + ss.getTarget().getID() + ">\n" +
                "Request by: <@" + ss.getRequestedBy().getID() + ">\n" +
                "Screenshared por: <@" + sender.getId() + ">\n\n" +
                "Reason do fechamento: `" + reason + "`");

        Objects.requireNonNull(RBWPlugin.guild.getTextChannelById(Config.getValue("ss-log-channel"))).sendMessageEmbeds(embed.build()).queue();
    }
}
