package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.sample.PickingMode;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.database.SQLUtilsManager;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Queue;
import com.kasp.rbw.instance.cache.QueueCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class AddQueueCmd extends Command {
    public AddQueueCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 5) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        VoiceChannel vc = null;
        String ID = args[1].replaceAll("[^0-9]","");
        try {vc = guild.getVoiceChannelById(ID);}catch (Exception ignored){}

        if (vc == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("invalid-vc"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int playersEachTeam = Integer.parseInt(args[2]);

        if (playersEachTeam <= 0) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("q-more-players"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (QueueCache.containsQueue(ID)) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("q-already-exists"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        PickingMode pickingMode = PickingMode.valueOf(args[3].toUpperCase());

        boolean casual = Boolean.parseBoolean(args[4]);

        SQLUtilsManager.createQueue(ID, playersEachTeam, pickingMode, casual);
        new Queue(ID);

        Embed embed = new Embed(EmbedType.SUCCESS, "✅ fila `" + vc.getName() + "` adicionada", "", 1);
        embed.addField("VC", vc.getAsMention(), true);
        embed.addField("Jogadores por Time:", args[2], true);
        embed.addField("Modo de Escolha:", args[3], true);
        embed.addField("É Casual:", casual + "", true);
        msg.replyEmbeds(embed.build()).queue();
    }
}
