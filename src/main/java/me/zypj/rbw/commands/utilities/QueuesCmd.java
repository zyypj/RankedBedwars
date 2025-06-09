package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.Queue;
import me.zypj.rbw.instance.cache.QueueCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class QueuesCmd extends Command {

    public QueuesCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (QueueCache.getQueues().isEmpty()) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "Você não tem nenhuma fila. Adicione uma `=addqueue`!", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Todas as filas", "isso também mostra **atualmente** jogadores na fila", 1);

        for (Queue q : QueueCache.getQueues().values()) {

            StringBuilder content = new StringBuilder("Aqui não tem nenhum jogador na fila");
            if (!q.getPlayers().isEmpty()) {
                content = new StringBuilder();

                for (Player p : q.getPlayers()) {
                    content.append("<@").append(p.getID()).append(">\n");
                }
            }

            embed.addField(Objects.requireNonNull(guild.getVoiceChannelById(q.getID())).getName() + " - `" + q.getPlayers().size() + "/" + q.getPlayersEachTeam() * 2 + "`",
                    content.toString(), false);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
