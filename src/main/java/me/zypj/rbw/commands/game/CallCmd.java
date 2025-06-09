package me.zypj.rbw.commands.game;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.cache.GameCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class CallCmd extends Command {
    public CallCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GameCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        if (sender.getVoiceState() == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "You are not in a vc", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.SUCCESS, "", "<@" + ID + "> has access to your vc", 1);

        Objects.requireNonNull(sender.getVoiceState().getChannel()).getManager().setUserLimit(sender.getVoiceState().getChannel().getUserLimit()+1).queue();
        Objects.requireNonNull(sender.getVoiceState().getChannel())
                .upsertPermissionOverride(Objects.requireNonNull(guild.getMemberById(ID)))
                .grant(Permission.VIEW_CHANNEL)
                .grant(Permission.VOICE_CONNECT)
                .queue();

        msg.replyEmbeds(embed.build()).queue();
    }
}
