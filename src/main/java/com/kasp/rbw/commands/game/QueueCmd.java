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

public class QueueCmd extends Command {
    public QueueCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Invalidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GameCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GameCache.getGame(channel.getId());

        int number = game.getNumber();

        StringBuilder t1 = new StringBuilder();
        for (Player p : game.getTeam1()) {
            t1.append("• <@").append(p.getID()).append(">\n");
        }
        StringBuilder t2 = new StringBuilder();
        for (Player p : game.getTeam2()) {
            t2.append("• <@").append(p.getID()).append(">\n");
        }

        StringBuilder remaining = new StringBuilder();
        for (Player p : game.getRemainingPlayers()) {
            remaining.append("• <@").append(p.getID()).append(">\n");
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Jogo `#" + number + "` Queue", "", 1);
        embed.addField("Time 1", t1.toString(), true);
        embed.addField("Time 2", t2.toString(), true);
        if (remaining.length() > 0) {
            embed.addField("Restando", remaining.toString(), false);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
