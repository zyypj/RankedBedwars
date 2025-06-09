package me.zypj.rbw.commands.player;

import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.ScreenShare;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;

public class ScreenshareCmd extends Command {
    public ScreenshareCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 3) {
            Embed reply = new Embed(
                    EmbedType.ERROR,
                    "Invalid Arguments",
                    Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()),
                    1
            );
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int requiredImages = Integer.parseInt(Config.getValue("ss-attachments"));
        if (msg.getAttachments().size() != requiredImages) {
            Embed reply = new Embed(
                    EmbedType.ERROR,
                    "Error",
                    "You need " + requiredImages + " images as proof",
                    1
            );
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String targetId = args[1].replaceAll("[^0-9]", "");
        String reason = msg.getContentRaw()
                .replaceAll(args[0], "")
                .replaceAll(args[1], "")
                .trim();

        Member target = guild.retrieveMemberById(targetId).complete();
        if (sender.equals(target)) {
            Embed reply = new Embed(
                    EmbedType.ERROR,
                    "Error",
                    Msg.getMsg("ss-self"),
                    1
            );
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        // Freeze the user
        Role frozenRole = guild.getRoleById(Config.getValue("frozen-role"));
        List<Role> freeze = Collections.singletonList(frozenRole);
        guild.modifyMemberRoles(target, freeze, null).queue();

        // Schedule unfreeze
        long delayMillis = Integer.parseInt(Config.getValue("time-till-unfrozen")) * 60_000L;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                guild.modifyMemberRoles(target, null, freeze).queue();
            }
        }, delayMillis);

        // Notify in the SS request channel
        Embed embed = new Embed(
                EmbedType.ERROR,
                "DO NOT LEAVE THE SERVER",
                "",
                1
        );
        embed.setDescription(
                target.getAsMention() + ", you have been summoned for a screenshare\n" +
                        "DO NOT LEAVE or modify/delete any files on your PC\n" +
                        "If staff does not show up, you are free to leave in " +
                        Config.getValue("time-till-unfrozen") + " minutes\n\n" +
                        "**Screenshare Reason**: " + reason + "\n\n" +
                        "**Requested by**: " + sender.getAsMention()
        );

        TextChannel ssChannel = Objects.requireNonNull(
                guild.getTextChannelById(Config.getValue("ssreq-channel"))
        );
        ssChannel.sendMessage(target.getAsMention())
                .setEmbeds(embed.build())
                .queue();

        if (!channel.getId().equals(ssChannel.getId())) {
            msg.reply("Screenshare request sent in " + ssChannel.getAsMention()).queue();
        }

        new ScreenShare(
                PlayerCache.getPlayer(sender.getId()),
                PlayerCache.getPlayer(targetId),
                reason
        );
    }
}