package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Rank;
import me.zypj.rbw.instance.cache.RankCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DeleteRankCmd extends Command {
    public DeleteRankCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Role role = null;
        String ID = args[1].replaceAll("[^0-9]","");
        try {role = guild.getRoleById(ID);}catch (Exception ignored){}

        if (role == null) {
            Embed error = new Embed(EmbedType.ERROR, "", Msg.getMsg("invalid-role"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (!RankCache.containsRank(ID)) {
            Embed error = new Embed(EmbedType.ERROR, "", Msg.getMsg("rank-doesnt-exist"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        Rank.delete(ID);

        Embed success = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("rank-deleted"), 1);
        msg.replyEmbeds(success.build()).queue();
    }
}
