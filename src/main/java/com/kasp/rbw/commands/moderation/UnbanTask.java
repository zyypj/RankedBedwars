package com.kasp.rbw.commands.moderation;

import com.kasp.rbw.EmbedType;
import com.kasp.rbw.RBW;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PlayerCache;

import java.time.LocalDateTime;
import java.util.Objects;

public class UnbanTask {

    public static void checkAndUnbanPlayers() {
        String unbanned = "";

        for (Player p : PlayerCache.getPlayers().values()) {
            if (p.isBanned()) {
                if (p.getBannedTill() == null || p.getBannedTill().isBefore(LocalDateTime.now())) {
                    p.unban();

                    unbanned += "<@" + p.getID() + "> (" + p.getIgn() + ") ";
                }
            }
        }

        if (unbanned != "") {
            if (!Objects.equals(Config.getValue("ban-channel"), null)) {
                Embed embed = new Embed(EmbedType.DEFAULT, "Unbanned some players `(auto)`:", unbanned, 1);
                RBW.getGuild().getTextChannelById(Config.getValue("ban-channel")).sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}
