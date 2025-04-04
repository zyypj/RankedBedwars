package com.kasp.rbw.listener;

import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.Queue;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.instance.cache.QueueCache;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class QueueJoin extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

        // leave vc
        if (event.getChannelJoined() != null) {
            if (QueueCache.containsQueue(event.getChannelJoined().getId())) {
                TextChannel alerts = event.getGuild().getTextChannelById(Config.getValue("alerts-channel"));

                String ID = event.getMember().getId();
                Queue queue = QueueCache.getQueue(event.getChannelJoined().getId());

                Player player = PlayerCache.getPlayer(ID);

                if (!Player.isRegistered(ID)) {
                    event.getGuild().kickVoiceMember(event.getMember()).queue();

                    Embed embed = new Embed(EmbedType.ERROR, "Você Não Pode Entrar na Fila", "", 1);
                    embed.setDescription("Você não está registrado. Por favor use `=register <seu nick>` e tente novamente");
                    assert alerts != null;
                    alerts.sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
                    return;
                }

                if (player.isBanned()) {
                    event.getGuild().kickVoiceMember(event.getMember()).queue();

                    Embed embed = new Embed(EmbedType.ERROR, "Você Não Pode Entrar na Fila", "", 1);
                    embed.addField("Isso aconteceu pois você está banido!", "Se isso é um erro, por favor use `=fix`. Se isso não remover seu cargo de banido, abra um ticket para Appeal", false);
                    assert alerts != null;
                    alerts.sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
                    return;
                }

                if (!player.isOnline()) {
                    event.getGuild().kickVoiceMember(event.getMember()).queue();

                    Embed embed = new Embed(EmbedType.ERROR, "Você Não Pode Entrar na Fila", "Você precisa estar online em `" + Config.getValue("server-ip") + "` para entrar na fila", 1);
                    assert alerts != null;
                    alerts.sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
                    return;
                }

                queue.addPlayer(player);
            }
        }
        // left vc
        if (event.getChannelLeft() != null) {
            if (QueueCache.containsQueue(event.getChannelLeft().getId())) {
                String ID = event.getMember().getId();
                Queue queue = QueueCache.getQueue(event.getChannelLeft().getId());

                Player player = PlayerCache.getPlayer(ID);

                queue.removePlayer(player);
            }
        }
    }
}
