package com.kasp.rbw.commands.game;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.GameState;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Game;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ForceVoidCmd extends Command {
    public ForceVoidCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game;

        if (args.length == 1) {
            if (GameCache.getGame(channel.getId()) == null) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-game-channel"), 1);
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

        Embed done = new Embed(EmbedType.DEFAULT, "Game`#" + game.getNumber() + "` Has Been Voided", "if this command was abused, please screenshot this and make a report ticket\n\ngame channel closing in `60s`", 1);
        done.addField("Voided by: ", sender.getAsMention(), false);

        msg.replyEmbeds(done.build()).queue();
    }
}
