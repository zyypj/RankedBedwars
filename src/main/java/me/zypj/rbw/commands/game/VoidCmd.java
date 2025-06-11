package me.zypj.rbw.commands.game;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.GameState;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Game;
import me.zypj.rbw.instance.cache.GameCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import me.zypj.rbw.RBWPlugin;
import org.bukkit.Bukkit;

public class VoidCmd extends Command {
    public VoidCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GameCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GameCache.getGame(channel.getId());
        int number = game.getNumber();

        Embed embed = new Embed(EmbedType.DEFAULT, "Game `#" + number + "` Voiding", "votes counted in `30s`", 1);
        embed.addField("Request by: ", sender.getAsMention(), false);

        channel.sendMessageEmbeds(embed.build()).queue(message -> {
            message.addReaction(Emoji.fromUnicode("✔")).queue();
            message.addReaction(Emoji.fromUnicode("❌")).queue();

            Bukkit.getScheduler().runTaskLater(RBWPlugin.getInstance(), () -> {
                Message message1 = channel.retrieveMessageById(message.getId()).complete();

                if (message1.getReactions().get(0).getCount() - 1 < message1.getReactions().get(1).getCount()) {
                    Embed reply = new Embed(EmbedType.ERROR, "", "Voiding has cancelled", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }

                Embed done = new Embed(EmbedType.DEFAULT, "Game `#" + number + "` has Voided", "If this command is misused, please take a print and send it to us in a ticket channel being deleted in `60s`", 1);
                done.addField("Request by: ", sender.getAsMention(), false);
                game.setState(GameState.VOIDED);
                game.setScoredBy(sender);
                game.closeChannel(60);

                message1.editMessageEmbeds(done.build()).queue();
                message1.clearReactions().queue();
            }, 20L * 30);
        });
    }
}