package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.GameMap;
import me.zypj.rbw.instance.cache.MapCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MapsCmd extends Command {
    public MapsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        StringBuilder maps = new StringBuilder();
        for (GameMap m : MapCache.getMaps().values()) {
            maps.append("`[").append(m.getArenaState()).append("]` ").append("**").append(m.getName()).append("** `(").append(m.getMaxPlayers()).append("v").append(m.getMaxPlayers()).append(")` — Altura: ").append(m.getHeight()).append(" (").append(m.getTeam1()).append(" vs ").append(m.getTeam2()).append(")\n");
        }

        Embed embed;

        if (maps.toString().isEmpty()) {
            embed = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("no-maps"), 1);
        }
        else {
            embed = new Embed(EmbedType.DEFAULT, "Todos mapas", maps.toString(), 1);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
