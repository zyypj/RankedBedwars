package me.zypj.rbw.commands.clan;

import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Clan;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.ClanCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;
import java.util.Arrays;

public class ClanSettingsCmd extends Command {
    public ClanSettingsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        String[] settings = {"private", "eloReq", "description", "icon", "theme"};
        String[] settingsValue = {"true/false", "number", "text", "attached 135x135 image", "attached 960x540 image"};
        String[] settingsDesc = {
                "make your clan private — only invited players can join\nor public — anyone can join the clan",
                "change the minimum elo required to join the clan — clan must be public to apply",
                "change the clan description",
                "change the clan icon",
                "change the theme for your clan’s =cstats command"
        };

        if (args.length < 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments",
                    Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());
        Clan clan = ClanCache.getClan(player);

        if (clan == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-clan"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (clan.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-clan-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String setting = args[1];
        if (!Arrays.asList(settings).contains(setting)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-setting"), 1);
            msg.replyEmbeds(reply.build()).queue();

            Embed embed = new Embed(EmbedType.DEFAULT, "Available Settings", "", 1);
            for (int i = 0; i < settings.length; i++) {
                embed.addField(
                        settings[i],
                        "Value — `" + settingsValue[i] + "`\n" + settingsDesc[i],
                        false
                );
            }
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        String value = "";
        int idx = Arrays.asList(settings).indexOf(setting);

        try {
            switch (setting) {
                case "private":
                    value = args[2];
                    clan.setPrivate(Boolean.parseBoolean(value));
                    break;

                case "eloReq":
                    value = args[2];
                    clan.setEloJoinReq(Integer.parseInt(value));
                    break;

                case "description":
                    String fullText = msg.getContentRaw()
                            .replace(args[0], "")
                            .replace(args[1], "")
                            .trim();
                    int maxLen = Integer.parseInt(Config.getValue("clan-desc-max"));
                    if (fullText.length() > maxLen) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                Msg.getMsg("desc-too-long"), 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    clan.setDescription(fullText);
                    value = fullText;
                    break;

                case "icon":
                    int iconLevelReq = Integer.parseInt(Config.getValue("allow-setting-icon"));
                    if (clan.getLevel().getLevel() < iconLevelReq) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "Your clan needs to be level " + iconLevelReq + " to change the icon", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (msg.getAttachments().isEmpty() || !msg.getAttachments().get(0).isImage()) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "You must attach a 135x135 image as your icon", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    var iconAttachment = msg.getAttachments().get(0);
                    if (!"icon.png".equals(iconAttachment.getFileName())) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "Please name the image file `icon.png`", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (iconAttachment.getWidth() != 135 || iconAttachment.getHeight() != 135) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "Image dimensions must be 135x135", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    iconAttachment.getProxy().downloadToFile(
                            new File(RBWPlugin.getInstance()
                                    .getDataFolder() + "/RankedBot/clans/"
                                    + clan.getName() + "/" + iconAttachment.getFileName())
                    );
                    value = "icon.png";
                    break;

                case "theme":
                    int themeLevelReq = Integer.parseInt(Config.getValue("allow-setting-theme"));
                    if (clan.getLevel().getLevel() < themeLevelReq) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "Your clan needs to be level " + themeLevelReq + " to change the theme", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (msg.getAttachments().isEmpty() || !msg.getAttachments().get(0).isImage()) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "You must attach a 960x540 image as your theme", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    var themeAttachment = msg.getAttachments().get(0);
                    if (!"theme.png".equals(themeAttachment.getFileName())) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "Please name the image file `theme.png`", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (themeAttachment.getWidth() != 960 || themeAttachment.getHeight() != 540) {
                        Embed reply = new Embed(EmbedType.ERROR, "Error",
                                "Image dimensions must be 960x540", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    themeAttachment.getProxy().downloadToFile(
                            new File(RBWPlugin.getInstance()
                                    .getDataFolder() + "/RankedBot/clans/"
                                    + clan.getName() + "/" + themeAttachment.getFileName())
                    );
                    value = "theme.png";
                    break;
            }
        } catch (Exception e) {
            Embed errorEmbed = new Embed(EmbedType.ERROR, "Error",
                    "Something went wrong… please use one of these values: `"
                            + settingsValue[idx] + "`", 1);
            msg.replyEmbeds(errorEmbed.build()).queue();
            return;
        }

        Embed successEmbed = new Embed(EmbedType.SUCCESS, "Settings Updated",
                "You set `" + value + "` for the `" + setting + "` setting", 1);
        msg.replyEmbeds(successEmbed.build()).queue();
    }
}
