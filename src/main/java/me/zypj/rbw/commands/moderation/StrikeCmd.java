package me.zypj.rbw.commands.moderation;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.LocalDateTime;
import java.util.Objects;

public class StrikeCmd extends Command {
    public StrikeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");
        String reason = msg.getContentRaw().replaceAll(args[0], "").replaceAll(args[1], "").trim();

        Player player = PlayerCache.getPlayer(ID);
        player.setStrikes(player.getStrikes()+1);

        int strikes = player.getStrikes();
        if (player.getStrikes() > 5) {
            strikes = 5;
        }

        int bantime = Integer.parseInt(Config.getValue("strike-" + strikes));

        Embed embed = new Embed(EmbedType.DEFAULT, player.getIgn() + " has strike", "", 1);
        embed.addField("Strikes:", player.getStrikes()-1 + " -> " + player.getStrikes(), false);
        embed.addField("Reason", reason, false);

        Embed success = new Embed(EmbedType.SUCCESS, "", "Stricked <@" + ID + ">", 1);

        if (bantime != 0) {
            if (player.ban(LocalDateTime.now().plusHours(bantime), "[STRIKE] " + reason)) {

                embed.addField("You have been banned por: ", "`" + bantime + " hours`", false);
                embed.setDescription("<@" + ID + "> Use `=fix` after " + bantime + "h to get yourself unbanned");
            }
            else {
                embed.addField("WARNING", "You cannot ban this player as he is already banned", false);
                success.addField("WARNING", "You cannot ban this player as he is already banned", false);
            }
        }

        msg.replyEmbeds(success.build()).queue();
        if (!Objects.equals(Config.getValue("ban-channel"), null)) {
            Objects.requireNonNull(guild.getTextChannelById(Config.getValue("ban-channel"))).sendMessage("<@" + ID + ">").setEmbeds(embed.build()).queue();
        }
    }
}