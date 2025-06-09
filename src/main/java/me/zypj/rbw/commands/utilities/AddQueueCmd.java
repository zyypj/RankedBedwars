package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.PickingMode;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.database.SQLUtilsManager;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Queue;
import me.zypj.rbw.instance.cache.QueueCache;
import me.zypj.rbw.messages.Msg;
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
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        VoiceChannel vc = null;
        String ID = args[1].replaceAll("[^0-9]","");
        try {vc = guild.getVoiceChannelById(ID);}catch (Exception ignored){}

        if (vc == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-vc"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int playersEachTeam = Integer.parseInt(args[2]);

        if (playersEachTeam <= 0) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("q-more-players"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (QueueCache.containsQueue(ID)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("q-already-exists"), 1);
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
