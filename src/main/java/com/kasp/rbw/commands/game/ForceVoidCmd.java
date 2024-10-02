package com.kasp.rbw.commands.game;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.sample.GameState;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Game;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ForceVoidCmd extends Command {
    public ForceVoidCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game;

        if (args.length == 1) {
            if (GameCache.getGame(channel.getId()) == null) {
                Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-game-channel"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            game = GameCache.getGame(channel.getId());
        }
        else {
            game = GameCache.getGame(Integer.parseInt(args[1]));
        }

        game.setState(GameState.VOIDED);
        game.setScoredBy(sender);

        game.closeChannel(60);

        Embed done = new Embed(EmbedType.DEFAULT, "Jogo `#" + game.getNumber() + "` Foi Voidado", "se esse comando for mal utilizado, /*por favor, tire print e nos mande em ticket\n*/\ncanal sendo excluido em `60s`", 1);
        done.addField("Voidado por: ", sender.getAsMention(), false);

        msg.replyEmbeds(done.build()).queue();
    }
}
