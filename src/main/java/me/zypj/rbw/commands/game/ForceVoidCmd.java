package me.zypj.rbw.commands.game;

import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Game;
import me.zypj.rbw.instance.cache.GameCache;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.GameState;
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
        } else {
            game = GameCache.getGame(Integer.parseInt(args[1]));
        }

        game.setState(GameState.VOIDED);
        game.setScoredBy(sender);

        game.closeChannel(60);

        Embed done = new Embed(EmbedType.DEFAULT, "Game `#" + game.getNumber() + "` Was Voidated", "if this command is misused, /*please take a print and send it to us in a ticket\n" +
                "*/\n" +
                "channel being deleted in `60s`", 1);
        done.addField("Void by: ", sender.getAsMention(), false);

        msg.replyEmbeds(done.build()).queue();
    }
}
