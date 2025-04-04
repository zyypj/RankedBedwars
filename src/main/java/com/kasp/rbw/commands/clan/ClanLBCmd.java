package com.kasp.rbw.commands.clan;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Clan;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Leaderboard;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.List;

public class ClanLBCmd extends Command {
    public ClanLBCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        List<Clan> lb = new ArrayList<>(Leaderboard.getClansLeaderboard());

        Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("carregando...").build()).complete();

        for (int j = 0; j < Math.ceil(lb.size() / 10.0); j++) {
            Embed reply = new Embed(EmbedType.DEFAULT, "Clans Leaderboard", "isso é a lb de `reputação`\nvocê obtem jogando\nclan wars", (int) Math.ceil(lb.size() / 10.0));

            StringBuilder lbmsg = new StringBuilder();
            for (int i = j * 10; i < j * 10 + 10; i++) {
                if (i < lb.size()) {
                    lbmsg.append("**#").append(i + 1).append("** `").append(lb.get(i).getName()).append("` — ").append(lb.get(i).getReputation()).append("\n");
                }
            }
            reply.setDescription(lbmsg.toString());

            if (j == 0) {
                embedmsg.editMessageEmbeds(reply.build())
                        .setComponents(ActionRow.of(Embed.createButtons(reply.getCurrentPage())))
                        .queue();
            }

            Embed.addPage(embedmsg.getId(), reply);
        }
    }
}
