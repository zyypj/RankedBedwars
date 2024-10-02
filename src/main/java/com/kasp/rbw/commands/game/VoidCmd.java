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
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Timer;
import java.util.TimerTask;

public class VoidCmd extends Command {
    public VoidCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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
        int number = game.getNumber();

        Embed embed = new Embed(EmbedType.DEFAULT, "Jogo `#" + number + "` Voiding", "votos contados em `30s`", 1);
        embed.addField("Pedido por: ", sender.getAsMention(), false);

        channel.sendMessageEmbeds(embed.build()).queue(message -> {
            message.addReaction(Emoji.fromUnicode("✔")).queue();
            message.addReaction(Emoji.fromUnicode("❌")).queue();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Message message1 = channel.retrieveMessageById(message.getId()).complete();

                    if (message1.getReactions().get(0).getCount() - 1 < message1.getReactions().get(1).getCount()) {
                        Embed reply = new Embed(EmbedType.ERROR, "", "Voiding foi cancelado", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }

                    Embed done = new Embed(EmbedType.DEFAULT, "Jogo `#" + number + "` Foi Voided", "se esse comando for mal utilizado, /*por favor, tire print e nos mande em ticket\n\ncanal sendo excluido em `60s`", 1);
                    done.addField("Pedido por: ", sender.getAsMention(), false);
                    game.setState(GameState.VOIDED);
                    game.setScoredBy(sender);
                    game.closeChannel(60);

                    message1.editMessageEmbeds(done.build()).queue();
                    message1.clearReactions().queue();
                }
            };

            Timer timer = new Timer();
            timer.schedule(task, 30000);
        });
    }
}