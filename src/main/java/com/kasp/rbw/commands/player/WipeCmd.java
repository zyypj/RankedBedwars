package com.kasp.rbw.commands.player;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.database.SQLPlayerManager;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class WipeCmd extends Command {
    public WipeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (args[1].equals("everyone")) {
            double time = SQLPlayerManager.getPlayerSize() / 20.0;

            Embed reply = new Embed(EmbedType.DEFAULT, "Resetting everyone's stats...", "`Check console for more details`", 1);
            reply.addField("WARNING", "please do not use any other cmd during the reset\nit might result in errors / slower resetting", false);
            reply.addField("Estimated time", time + " second(s) `(" + SQLPlayerManager.getPlayerSize() + " players)`", false);
            reply.addField("Reset by:", sender.getAsMention(), true);
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

            Embed success = new Embed(EmbedType.SUCCESS, "All stats were successfully reset", "", 1);
            success.addField("Resetting took", "`" + elapsedTime + "` seconds `(" + SQLPlayerManager.getPlayerSize() + " players)`", true);
            success.addField("Reset by:", sender.getAsMention(), true);
            msg.replyEmbeds(success.build()).queue();
        }
        else {
            Player player = PlayerCache.getPlayer(args[1].replaceAll("[^0-9]", ""));
            player.wipe();
            player.fix();

            Embed reply = new Embed(EmbedType.SUCCESS, "Stats wiped", Msg.getMsg("successfully-wiped"), 1);
            msg.replyEmbeds(reply.build()).queue();
        }
    }
}
