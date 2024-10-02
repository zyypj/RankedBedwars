package com.kasp.rbw.commands.game;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.sample.GameState;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Game;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class UndoGameCmd extends Command {
    public UndoGameCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int number = Integer.parseInt(args[1]);

        if (GameCache.getGame(number) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("invalid-game"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GameCache.getGame(number);

        if (game.getState() != GameState.SCORED) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-scored"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        game.undo();

        Embed embed = new Embed(EmbedType.SUCCESS, "Jogo `#" + game.getNumber() + "` foi despontoado", "você pode pontuar novamente usando `=score` de novo", 1);
        msg.replyEmbeds(embed.build()).queue();

        embed.setType(EmbedType.ERROR);

        embed.addField("Despontoado por", sender.getAsMention(), false);
        embed.setDescription("");

        if (!Objects.equals(Config.getValue("scored-announcing"), null)) {
            Objects.requireNonNull(guild.getTextChannelById(Config.getValue("scored-announcing"))).sendMessageEmbeds(embed.build()).queue();
        }
    }
}
