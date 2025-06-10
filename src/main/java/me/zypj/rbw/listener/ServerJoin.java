package me.zypj.rbw.listener;

import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PlayerCache;
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

            Embed embed = new Embed(EmbedType.DEFAULT, "Welcome back", "Good winds tell me this isn't your first time here" +
                    "\nI've given you back your stats and nickname - you don't need to register again!", 1);

            Objects.requireNonNull(event.getGuild().getTextChannelById(Config.getValue("alerts-channel"))).sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
        }
    }
}
