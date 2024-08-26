package com.kasp.rbw.commands.player;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.LinkManager;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

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

        /*SQLPlayerManager.createPlayer(sender.getId(), ign);
        Player player = new Player(sender.getId());
        player.fix();

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("successfully-registered"), 1);
        msg.replyEmbeds(reply.build()).queue();*/

        int code = LinkManager.addPlayer(sender.getId());

        Embed reply;
        if (code != -1) {
             reply = new Embed(EmbedType.SUCCESS, "Complete Registration",
                    "**Please follow these steps**\n" +
                            "`1` Log onto `" + Config.getValue("server-ip") + "` mc server\n" +
                            "`2` Use `/register " + code + "` there\n\n" +
                            "**This code will be deleted after 5 minutes**\n" +
                            "To generate a new code simply use `=register` again", 1);
        }
        else {
            reply = new Embed(EmbedType.ERROR, "Error", "You have already generated a code\nPlease wait 5 minutes and then try again", 1);
        }

        msg.replyEmbeds(reply.build()).queue();
    }
}
