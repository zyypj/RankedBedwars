package com.kasp.rbw.listener;

import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PlayerCache;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ServerJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        if (Player.isRegistered(event.getMember().getId())) {
            Player player = PlayerCache.getPlayer(event.getMember().getId());
            player.fix();

            Embed embed = new Embed(EmbedType.DEFAULT, "Seja bem vindo de volta", "Bons ventos me disseram que não é a sua primeira vez aqui" +
                    "\nEu devolvi suas estatísticas e seu apelido - você não precisa se registrar novamente!", 1);

            Objects.requireNonNull(event.getGuild().getTextChannelById(Config.getValue("alerts-channel"))).sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
        }
    }
}
