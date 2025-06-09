package me.zypj.rbw.commands.game;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.GameState;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Game;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.Queue;
import me.zypj.rbw.instance.cache.GameCache;
import me.zypj.rbw.instance.cache.QueueCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SubmitCmd extends Command {
    public SubmitCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inváligos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GameCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (msg.getAttachments().size() != Integer.parseInt(Config.getValue("submitting-attachments"))) {
            Embed error = new Embed(EmbedType.ERROR, "", "You need to send a print " + Config.getValue("submitting-attachments") + " as proof", 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        Game game = GameCache.getGame(channel.getId());

        if (game.getState() != GameState.PLAYING) {
            Embed error = new Embed(EmbedType.ERROR, "", "You can't do this now", 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (game.isCasual()) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("casual-game"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.SUCCESS, "Game `#" + game.getNumber() + "` finished", "", 1);

        StringBuilder queues = new StringBuilder();
        for (Queue q : QueueCache.getQueues().values()) {
            queues.append("<#").append(q.getID()).append(">\n");
        }
        embed.addField("Enter again here:", queues.toString(), false);
        embed.setFooter("RankedBW");
        channel.sendMessageEmbeds(embed.build()).queue();

        game.setState(GameState.SUBMITTED);

        Embed scoring = new Embed(EmbedType.DEFAULT, "Game `#" + game.getNumber() + "` Scored", "", 1);

        StringBuilder t1 = new StringBuilder();
        StringBuilder t2 = new StringBuilder();
        for (Player p : game.getTeam1())
            t1.append("• <@").append(p.getID()).append(">\n");
        for (Player p : game.getTeam2())
            t2.append("• <@").append(p.getID()).append(">\n");

        scoring.addField("Team 1:", t1.toString(), true);
        scoring.addField("Team 2:", t2.toString(), true);

        if (!msg.getAttachments().isEmpty()) {
            scoring.setImageURL(msg.getAttachments().get(0).getUrl());
        }

        scoring.setDescription("Map: `" + game.getMap().getName() + "`\n\nUse `=score` to score this game");

        channel.sendMessage("<@&" + Config.getValue("scorer-role") + ">").setEmbeds(scoring.build()).queue();
    }
}
