package com.kasp.rbw.commands.game;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Game;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class QueueStatsCmd extends Command {
    public QueueStatsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GameCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GameCache.getGame(channel.getId());

        StringBuilder t1 = new StringBuilder();
        for (Player p : game.getTeam1()) {
            double templosses = 1;
            if (p.getLosses() > 0)
                templosses = p.getLosses();

            double wlr = p.getWins() / templosses;
            t1.append("• <@").append(p.getID()).append("> — `").append(p.getWins()).append("W/").append(p.getLosses()).append("L` `(").append(wlr).append("WLR)`\n");
        }

        StringBuilder t2 = new StringBuilder();
        for (Player p : game.getTeam2()) {
            double templosses = 1;
            if (p.getLosses() > 0)
                templosses = p.getLosses();

            double wlr = p.getWins() / templosses;
            t2.append("• <@").append(p.getID()).append("> — `").append(p.getWins()).append("W/").append(p.getLosses()).append("L` `(").append(wlr).append("WLR)`\n");
        }

        StringBuilder remaining = new StringBuilder();
        for (Player p : game.getRemainingPlayers()) {
            double templosses = 1;
            if (p.getLosses() > 0)
                templosses = p.getLosses();

            double wlr = p.getWins() / templosses;
            remaining.append("• <@").append(p.getID()).append("> — `").append(p.getWins()).append("W/").append(p.getLosses()).append("L` `(").append(wlr).append("WLR)`\n");
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Jogo `#" + game.getNumber() + "` QueueStats", "", 1);
        embed.addField("Time 1", t1.toString(), true);
        embed.addField("Time 2", t2.toString(), true);
        if (remaining.length() > 0) {
            embed.addField("Restando", remaining.toString(), false);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
