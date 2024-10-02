package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Rank;
import com.kasp.rbw.instance.cache.RankCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RanksCmd extends Command {
    public RanksCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        List<String> ranks = new ArrayList<>();

        for (Rank r : RankCache.getRanks().values()) {
            ranks.add("<@&" + r.getID() + "> {" + r.getStartingElo() + "} - " + r.getEndingElo() + "** `+" + r.getWinElo() + " / -" + r.getLoseElo() + "` MVP: " + r.getMvpElo() + "");
        }

        ranks.sort(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return extractInt(o2) - extractInt(o1);
            }

            int extractInt(String s) {
                String num = s.substring(s.indexOf("{") + 1, s.indexOf("}"));
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });

        String display = "";
        Embed embed;

        if (ranks.isEmpty()) {
            embed = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("no-ranks"), 1);
        }
        else {
            for (String role : ranks) {
                display += role.replaceAll("\\{", "**").replaceAll("}", "") + "\n";
            }
            embed = new Embed(EmbedType.DEFAULT, "Todos Ranks", display, 1);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
