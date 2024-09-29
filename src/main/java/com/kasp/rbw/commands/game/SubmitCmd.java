package com.kasp.rbw.commands.game;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.sample.GameState;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Game;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.Queue;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.instance.cache.QueueCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (msg.getAttachments().size() != Integer.parseInt(Config.getValue("submitting-attachments"))) {
            Embed error = new Embed(EmbedType.ERROR, "", "Você precisa enviar uma print " + Config.getValue("submitting-attachments") + " como prova", 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        Game game = GameCache.getGame(channel.getId());

        if (game.getState() != GameState.PLAYING) {
            Embed error = new Embed(EmbedType.ERROR, "", "Você não pode fazer isso agora", 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (game.isCasual()) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("casual-game"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.SUCCESS, "Jogo `#" + game.getNumber() + "` finalizado", "", 1);

        String queues = "";
        for (Queue q : QueueCache.getQueues().values()) {
            queues += "<#" + q.getID() + ">\n";
        }
        embed.addField("Entre novamente aquo:", queues, false);
        embed.setFooter("Academy Ranked (PulseMC)");
        channel.sendMessageEmbeds(embed.build()).queue();

        game.setState(GameState.SUBMITTED);

        Embed scoring = new Embed(EmbedType.DEFAULT, "Jogo `#" + game.getNumber() + "` Pontuado", "", 1);

        StringBuilder t1 = new StringBuilder();
        StringBuilder t2 = new StringBuilder();
        for (Player p : game.getTeam1())
            t1.append("• <@").append(p.getID()).append(">\n");
        for (Player p : game.getTeam2())
            t2.append("• <@").append(p.getID()).append(">\n");

        scoring.addField("Time 1:", t1.toString(), true);
        scoring.addField("Time 2:", t2.toString(), true);

        if (!msg.getAttachments().isEmpty()) {
            scoring.setImageURL(msg.getAttachments().get(0).getUrl());
        }

        scoring.setDescription("Mapa: `" + game.getMap().getName() + "`\n\nUse `=score` para pontuar esse jogo");

        channel.sendMessage("<@&" + Config.getValue("scorer-role") + ">").setEmbeds(scoring.build()).queue();
    }
}
