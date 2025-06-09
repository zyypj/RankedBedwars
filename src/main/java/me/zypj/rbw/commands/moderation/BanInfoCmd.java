package me.zypj.rbw.commands.moderation;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BanInfoCmd extends Command {
    public BanInfoCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        Player player = PlayerCache.getPlayer(ID);

        if (!player.isBanned()) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("player-not-banned"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

        long diffDays = ChronoUnit.DAYS.between(LocalDateTime.now(), player.getBannedTill());
        long diffHours = ChronoUnit.HOURS.between(LocalDateTime.now(), player.getBannedTill());
        long diffMins = ChronoUnit.MINUTES.between(LocalDateTime.now(), player.getBannedTill());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String desc = "Desbanido em `" + formatter.format(player.getBannedTill()) + " UTC-4`\nTempo até ser desbanido `≈ " + diffDays + "d / " + diffHours + "h / " + diffMins + "m`\n**Reason: **" + player.getBanReason();

        Embed embed = new Embed(EmbedType.DEFAULT, "Banimento de " + player.getIgn(), desc, 1);
        msg.replyEmbeds(embed.build()).queue();
    }
}
