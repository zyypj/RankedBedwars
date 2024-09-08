package com.kasp.rbw.commands.player;

import com.kasp.rbw.commands.Command;
import com.kasp.rbw.database.SQLPlayerManager;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.messages.Msg;
import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ForceUnregisterCmd extends Command {
    public ForceUnregisterCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (!Player.isRegistered(ID)) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", "Esse jogador não está registrado", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        SQLPlayerManager.unregisterPlayer(ID);

        Embed reply = new Embed(EmbedType.SUCCESS, "", "Você desregistrou " + guild.getMemberById(ID).getAsMention() + " com sucesso!", 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
