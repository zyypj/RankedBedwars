package com.kasp.rbw.commands.moderation;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class UnbanCmd extends Command {
    public UnbanCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        Player player = PlayerCache.getPlayer(ID);

        if (!player.isBanned()) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("player-not-banned"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        player.unban();

        Embed embed = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("player-unbanned"), 1);
        msg.replyEmbeds(embed.build()).queue();
    }
}
