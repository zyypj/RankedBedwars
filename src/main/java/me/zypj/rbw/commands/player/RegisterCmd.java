package me.zypj.rbw.commands.player;

import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.LinkManager;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class RegisterCmd extends Command {

    public RegisterCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {

        if (Player.isRegistered(sender.getId())) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-registered"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int code = LinkManager.addPlayer(sender.getId());

        Embed reply;
        if (code != -1) {
            reply = new Embed(
                    EmbedType.SUCCESS,
                    "Complete Registration",
                    "**Follow the Instructions**\n" +
                            "`1` Log in to `" + Config.getValue("server-ip") + "`\n" +
                            "`2` Use `/register " + code + "` there\n\n" +
                            "**The code will expire in 5 minutes**\n" +
                            "To generate a new one, use `=register` again",
                    1
            );
        } else {
            reply = new Embed(
                    EmbedType.ERROR,
                    "Error",
                    "You already have a pending code\nTry again in 5 minutes",
                    1
            );
        }

        msg.replyEmbeds(reply.build()).queue();
    }
}
