package com.kasp.rbw.commands.player;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.ScreenShare;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenshareCmd extends Command {
    public ScreenshareCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (msg.getAttachments().size() != Integer.parseInt(Config.getValue("ss-attachments"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", "Você precisa de " + Config.getValue("ss-attachments") + " imagens como prova", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]","");
        String reason = msg.getContentRaw().replaceAll(args[0], "").replaceAll(args[1], "").trim();

        if (sender == guild.retrieveMemberById(ID).complete()) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("ss-self"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        List<Role> freeze = new ArrayList<>();
        freeze.add(guild.getRoleById(Config.getValue("frozen-role")));
        guild.modifyMemberRoles(guild.retrieveMemberById(ID).complete(), freeze, null).queue();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                guild.modifyMemberRoles(guild.retrieveMemberById(ID).complete(), null, freeze).queue();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, Integer.parseInt(Config.getValue("time-till-unfrozen")) * 60000L);

        Embed embed = new Embed(EmbedType.ERROR, "NÃO SAIA DO SERVIDOR", "", 1);
        embed.setDescription(guild.retrieveMemberById(ID).complete().getAsMention() + " você foi chamado para uma SS\nNÃO SAIA ou modifique/delete qualquer arquivo no seu pc\n" +
                "se o staff não aparecer, você está livre para sair em " + Config.getValue("time-till-unfrozen") + "mins\n\n" +
                "**Motivo SS**: " + reason + "\n\n**Pedido por**: " + sender.getAsMention());

        guild.getTextChannelById(Config.getValue("ssreq-channel")).sendMessage(guild.retrieveMemberById(ID).complete().getAsMention()).setEmbeds(embed.build()).queue();

        if (!channel.getId().equals(Config.getValue("ssreq-channel"))) {
            msg.reply("screenshare request sent in " + guild.getTextChannelById(Config.getValue("ssreq-channel")).getAsMention()).queue();
        }

        new ScreenShare(PlayerCache.getPlayer(sender.getId()), PlayerCache.getPlayer(ID), reason);
    }
}
