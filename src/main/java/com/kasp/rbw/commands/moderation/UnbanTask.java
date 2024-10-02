package com.kasp.rbw.commands.moderation;

import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.RBW;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PlayerCache;

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
                Embed embed = new Embed(EmbedType.DEFAULT, "Jogadores desbanidos `(auto)`:", unbanned.toString(), 1);
                Objects.requireNonNull(RBW.getGuild().getTextChannelById(Config.getValue("ban-channel"))).sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}
