package com.kasp.rbw.commands.server;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.levelsfile.Levels;
import com.kasp.rbw.messages.Msg;
import com.kasp.rbw.perms.Perms;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReloadConfigCmd extends Command {
    public ReloadConfigCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Config.reload();
        Msg.reload();
        Perms.reload();
        Levels.reload();

        Embed reply = new Embed(EmbedType.SUCCESS, "Reloaded", "New values were successfully loaded into memory", 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
