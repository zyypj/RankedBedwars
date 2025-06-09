package me.zypj.rbw.commands.player;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class FixCmd extends Command {
    public FixCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID;
        if (args.length == 2) {
            ID = args[1].replaceAll("[^0-9]", "");
        }
        else {
            ID = sender.getId();
        }

        Player player = PlayerCache.getPlayer(ID);
        player.fix();

        Embed reply = new Embed(EmbedType.SUCCESS, "", "Roles and updated nickname of `" + player.getIgn() + "`", 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
