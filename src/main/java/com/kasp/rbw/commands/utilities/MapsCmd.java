package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.GameMap;
import com.kasp.rbw.instance.cache.MapCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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

        String maps = "";
        for (GameMap m : MapCache.getMaps().values()) {
            maps += "`[" + m.getArenaState() + "]` " + "**" + m.getName() + "** `(" + m.getMaxPlayers() + "v" + m.getMaxPlayers() + ")` — Height: " + m.getHeight() + " (" + m.getTeam1() + " vs " + m.getTeam2() + ")\n";
        }

        Embed embed;

        if (maps.equals("")) {
            embed = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("no-maps"), 1);
        }
        else {
            embed = new Embed(EmbedType.DEFAULT, "All maps", maps, 1);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
