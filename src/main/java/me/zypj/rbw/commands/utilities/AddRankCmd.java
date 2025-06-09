package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.database.SQLUtilsManager;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Rank;
import me.zypj.rbw.instance.cache.RankCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class AddRankCmd extends Command {
    public AddRankCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 7) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Role role = null;
        String ID = args[1].replaceAll("[^0-9]","");
        try {role = guild.getRoleById(ID);}catch (Exception ignored){}

        if (role == null) {
            Embed error = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-role"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (RankCache.containsRank(ID)) {
            Embed error = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("rank-already-exists"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        String startingElo = args[2];
        String endingElo = args[3];
        String winElo = args[4];
        String loseElo = args[5];
        String mvpElo = args[6];

        SQLUtilsManager.createRank(ID, startingElo, endingElo, winElo, loseElo, mvpElo);
        new Rank(ID);

        Embed success = new Embed(EmbedType.SUCCESS, "✅ rank `" + role.getName() + "` adicionado", "", 1);
        success.addField("Cargo:", role.getAsMention(), true);
        success.addField("Elo inicial:", startingElo, true);
        success.addField("Elo final:", endingElo, true);
        success.addField("Elo por vítoria:", "+" + winElo, true);
        success.addField("Elo por derrota:", "-" + loseElo, true);
        success.addField("Mvp Elo:", mvpElo, true);

        msg.replyEmbeds(success.build()).queue();
    }
}
