package me.zypj.rbw.commands.player;

import me.zypj.rbw.commands.Command;
import me.zypj.rbw.database.SQLPlayerManager;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class ForceUnregisterCmd extends Command {
    public ForceUnregisterCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        if (!Player.isRegistered(ID)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "Esse jogador não está registrado", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        SQLPlayerManager.unregisterPlayer(ID);

        Embed reply = new Embed(EmbedType.SUCCESS, "", "You unregistered " + Objects.requireNonNull(guild.getMemberById(ID)).getAsMention() + "!", 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
