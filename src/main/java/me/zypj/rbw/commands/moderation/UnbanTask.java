package me.zypj.rbw.commands.moderation;

import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PlayerCache;

import java.time.LocalDateTime;
import java.util.Objects;

public class UnbanTask {

    public static void checkAndUnbanPlayers() {
        StringBuilder unbanned = new StringBuilder();

        for (Player p : PlayerCache.getPlayers().values()) {
            if (p.isBanned()) {
                if (p.getBannedTill() == null || p.getBannedTill().isBefore(LocalDateTime.now())) {
                    p.unban();

                    unbanned.append("<@").append(p.getID()).append("> (").append(p.getIgn()).append(") ");
                }
            }
        }

        if (!unbanned.toString().isEmpty()) {
            if (!Objects.equals(Config.getValue("ban-channel"), null)) {
                Embed embed = new Embed(EmbedType.DEFAULT, "Unbanned players `(auto)`:", unbanned.toString(), 1);
                Objects.requireNonNull(RBWPlugin.getGuild().getTextChannelById(Config.getValue("ban-channel"))).sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}
