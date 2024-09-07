package com.kasp.rbw.commands.player;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.LinkManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RenameCmd extends Command {
    public RenameCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {

        /*Player player = PlayerCache.getPlayer(sender.getId());
        player.setIgn(ign);
        player.fix();

        Embed reply = new Embed(EmbedType.SUCCESS, "", "You have successfully renamed to `" + ign + "`", 1);
        msg.replyEmbeds(reply.build()).queue();*/

        int code = LinkManager.addPlayer(sender.getId());

        Embed reply;
        if (code != -1) {
            reply = new Embed(EmbedType.SUCCESS, "Completar o Registro",
                    "**Siga as Instruções**\n" +
                            "`1` Logue em `" + Config.getValue("server-ip") + "`\n" +
                            "`2` Use `/rename " + code + "` ali\n\n" +
                            "**Esse código será apagado em 5 minutos**\n" +
                            "Para gerar um novo, use `=rename` novamente", 1);
        }
        else {
            reply = new Embed(EmbedType.ERROR, "Erro", "Você já tem um código pendente\nTente novamente em 5 minutos", 1);
        }

        msg.replyEmbeds(reply.build()).queue();
    }
}
