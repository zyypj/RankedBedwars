package me.zypj.rbw.commands.player;

import me.zypj.rbw.commands.Command;
import me.zypj.rbw.database.SQLPlayerManager;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class WipeCmd extends Command {
    public WipeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 2) {
            Embed reply = new Embed(
                    EmbedType.ERROR,
                    "Invalid Arguments",
                    Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()),
                    1
            );
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if ("everyone".equals(args[1])) {
            double time = SQLPlayerManager.getPlayerSize() / 20.0;

            Embed reply = new Embed(
                    EmbedType.DEFAULT,
                    "Resetting All Stats...",
                    "`Check the console for more information`",
                    1
            );
            reply.addField(
                    "WARNING",
                    "Do not use any other commands in the meantime\nThis may result in errors",
                    false
            );
            reply.addField(
                    "Estimated Time",
                    time + " second(s) `(" + SQLPlayerManager.getPlayerSize() + " players)`",
                    false
            );
            reply.addField(
                    "Reset by:",
                    sender.getAsMention(),
                    true
            );
            msg.replyEmbeds(reply.build()).queue();

            long start = System.currentTimeMillis();

            for (Player p : PlayerCache.getPlayers().values()) {
                p.wipe();
                if (guild.getMemberById(p.getID()) != null) {
                    p.fix();
                }
                System.out.println("[=wipe] successfully reset " + p.getIgn() + " (" + p.getID() + ")");
            }

            long end = System.currentTimeMillis();
            float elapsedTime = (end - start) / 1000F;

            Embed success = new Embed(
                    EmbedType.SUCCESS,
                    "All stats have been reset",
                    "",
                    1
            );
            success.addField(
                    "Resetting took",
                    "`" + elapsedTime + "` seconds `(" + SQLPlayerManager.getPlayerSize() + " players)`",
                    true
            );
            success.addField(
                    "Reset by:",
                    sender.getAsMention(),
                    true
            );
            msg.replyEmbeds(success.build()).queue();
        } else {
            Player player = PlayerCache.getPlayer(args[1].replaceAll("[^0-9]", ""));
            player.wipe();
            player.fix();

            Embed reply = new Embed(
                    EmbedType.SUCCESS,
                    "Stats wiped",
                    Msg.getMsg("successfully-wiped"),
                    1
            );
            msg.replyEmbeds(reply.build()).queue();
        }
    }
}