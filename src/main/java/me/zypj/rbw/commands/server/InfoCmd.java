package me.zypj.rbw.commands.server;

import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Game;
import me.zypj.rbw.instance.Queue;
import me.zypj.rbw.instance.cache.GameCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.instance.cache.QueueCache;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.GameState;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class InfoCmd extends Command {
    public InfoCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(
                    EmbedType.ERROR,
                    "Invalid Arguments",
                    Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()),
                    1
            );
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(
                EmbedType.DEFAULT,
                "Server Info",
                "RBW (PulseMC) by tadeu",
                1
        );

        // Total registered vs total members
        embed.addField(
                "Players",
                "`" + PlayerCache.getPlayers().size() + "` registered | `" + guild.getMemberCount() + "` total",
                false
        );

        // Count queued and playing players
        int queuedCount = 0;
        for (Queue q : QueueCache.getQueues().values()) {
            queuedCount += q.getPlayers().size();
        }
        int playingCount = 0;
        for (Game g : GameCache.getGames().values()) {
            if (g.getState() == GameState.PLAYING) {
                playingCount += g.getPlayers().size();
            }
        }

        embed.addField(
                "Current Activity",
                "`" + queuedCount + "` in queue | `" + playingCount + "` playing",
                false
        );

        // RAM usage
        long freeMb = Runtime.getRuntime().freeMemory()  / 1048576;
        long maxMb  = Runtime.getRuntime().maxMemory()   / 1048576;
        embed.addField(
                "RAM Usage",
                "`" + freeMb + "`/`" + maxMb + "` MB",
                false
        );

        msg.replyEmbeds(embed.build()).queue();
    }
}
