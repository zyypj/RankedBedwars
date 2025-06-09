package me.zypj.rbw.commands.player;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.Statistic;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Leaderboard;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardCmd extends Command {
    public LeaderboardCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        NumberFormat formatter = new DecimalFormat("#0");
        Statistic statistic = Statistic.ELO;
        if (args.length > 1) {
            try {
                statistic = Statistic.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                StringBuilder stats = new StringBuilder();
                for (Statistic s : Statistic.values()) {
                    stats.append("`").append(s).append("` ");
                }
                Embed embed = new Embed(EmbedType.ERROR, "Error", "This statistic does not exist\n" +
                        "Available: " + stats, 1);
                msg.replyEmbeds(embed.build()).queue();
                return;
            }
        }

        List<String> lb = new ArrayList<>(Leaderboard.getLeaderboard(statistic));

        Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("Loading...").build()).complete();

        for (int j = 0; j < Math.ceil(lb.size() / 10.0); j++) {

            Embed reply = new Embed(EmbedType.DEFAULT, statistic + " Leaderboard", "", (int) Math.ceil(lb.size() / 10.0));

            StringBuilder lbmsg = new StringBuilder();
            for (int i = j * 10; i < j * 10 + 10; i++) {
                if (i < lb.size()) {
                    String[] values = lb.get(i).split("=");
                    Player p = PlayerCache.getPlayer(values[0]);
                    lbmsg.append("**#").append(i + 1).append("** `").append(p.getIgn()).append("` — ").append(formatter.format(Double.parseDouble(values[1]))).append("\n");
                }
            }
            reply.setDescription(lbmsg.toString());

            if (j == 0) {
                embedmsg.editMessageEmbeds(reply.build()).setActionRow(Embed.createButtons(reply.getCurrentPage())).queue();
            }

            Embed.addPage(embedmsg.getId(), reply);
        }
    }
}
