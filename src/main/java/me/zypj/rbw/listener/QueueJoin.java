package me.zypj.rbw.listener;

import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.Queue;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.instance.cache.QueueCache;
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

                    Embed embed = new Embed(EmbedType.ERROR, "You Cannot Join the Queue", "", 1);
                    embed.setDescription("You are not registered. Please use `=register <your nickname>` and try again");
                    assert alerts != null;
                    alerts.sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
                    return;
                }

                if (player.isBanned()) {
                    event.getGuild().kickVoiceMember(event.getMember()).queue();

                    Embed embed = new Embed(EmbedType.ERROR, "You Cannot Join the Queue", "", 1);
                    embed.addField("This happened because you are banned!", "If this is an error, please use `=fix`. If this does not remove your banned status, please open an Appeal ticket", false);
                    assert alerts != null;
                    alerts.sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
                    return;
                }

                if (!player.isOnline()) {
                    event.getGuild().kickVoiceMember(event.getMember()).queue();

                    Embed embed = new Embed(EmbedType.ERROR, "You Cannot Join the Queue", "You must be online at `" + Config.getValue("server-ip") + "` to join the queue", 1);
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
