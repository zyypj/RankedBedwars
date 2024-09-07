package com.kasp.rbw.instance;

import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.RBW;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.cache.ScreenshareCache;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;

public class ScreenShare {

    String channelID;
    Player requestedBy;
    Player target;
    String reason;

    public ScreenShare(Player requestedBy, Player target, String reason) {
        this.requestedBy = requestedBy;
        this.target = target;
        this.reason = reason;
        Category ssCategory = RBW.guild.getCategoryById(Config.getValue("ss-channels-category"));

        channelID = ssCategory.createTextChannel("ss-" + target.getIgn()).complete().getId();
        RBW.guild.getTextChannelById(channelID).createPermissionOverride(RBW.guild.getMemberById(target.getID())).setAllow(Permission.VIEW_CHANNEL).queue();

        Embed embed = new Embed(EmbedType.DEFAULT, target.getIgn() + " screenshare", "", 1);
        embed.setDescription("Pedido por: <@" + requestedBy.getID() + ">\n" +
                "Suspeito: <@" + target.getID() + ">\n" +
                "Motivo: " + reason + "\n\n" +
                "Por favor, use `=ssclose <reason (outcome)>` depois de terminar a ScreenShare");

        String roles = "";
        for (String s : Config.getValue("ss-roles").split(",")) {
            roles+=RBW.guild.getRoleById(s).getAsMention();
        }

        RBW.guild.getTextChannelById(channelID).sendMessage(roles + " <@" + target.getID() + ">").setEmbeds(embed.build()).queue();

        ScreenshareCache.initializeScreenshare(this);
    }

    public String getChannelID() {
        return channelID;
    }

    public Player getRequestedBy() {
        return requestedBy;
    }

    public Player getTarget() {
        return target;
    }

    public String getReason() {
        return reason;
    }
}
