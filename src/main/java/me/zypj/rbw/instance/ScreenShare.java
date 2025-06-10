package me.zypj.rbw.instance;

import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.cache.ScreenshareCache;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class ScreenShare {

    String channelID;
    Player requestedBy;
    Player target;
    String reason;

    public ScreenShare(Player requestedBy, Player target, String reason) {
        this.requestedBy = requestedBy;
        this.target = target;
        this.reason = reason;
        Category ssCategory = RBWPlugin.guild.getCategoryById(Config.getValue("ss-channels-category"));

        assert ssCategory != null;
        TextChannel textChannel = ssCategory.createTextChannel("ss-" + target.getIgn()).complete();
        channelID = textChannel.getId();

        textChannel.upsertPermissionOverride(Objects.requireNonNull(RBWPlugin.guild.getMemberById(target.getID())))
                .setAllowed(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND) // Definir a permissão de visualização
                .queue();

        Embed embed = new Embed(EmbedType.DEFAULT, target.getIgn() + " screenshare", "", 1);
        embed.setDescription("Request by: <@" + requestedBy.getID() + ">\n" +
                "Suspicious: <@" + target.getID() + ">\n" +
                "Reason: " + reason + "\n\n" +
                "Please use `=ssclose <reason (outcome)>` after finishing ScreenShare");

        StringBuilder roles = new StringBuilder();
        for (String s : Config.getValue("ss-roles").split(",")) {
            roles.append(Objects.requireNonNull(RBWPlugin.guild.getRoleById(s)).getAsMention());
        }

        textChannel.sendMessage(roles + " <@" + target.getID() + ">").setEmbeds(embed.build()).queue();

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