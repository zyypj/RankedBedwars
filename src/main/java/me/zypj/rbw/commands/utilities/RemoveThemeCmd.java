package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.Theme;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.instance.cache.ThemeCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Objects;

public class RemoveThemeCmd extends Command {
    public RemoveThemeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String name = args[2];

        if (!ThemeCache.containsTheme(name)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("theme-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Theme theme = ThemeCache.getTheme(name);

        String ID = args[1].replaceAll("[^0-9]", "");

        Player player = PlayerCache.getPlayer(ID);

        if (!player.getOwnedThemes().contains(theme)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("doesnt-have-theme"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        player.removeTheme(theme);

        Embed embed = new Embed(EmbedType.SUCCESS, "", "Você removeu o tema `" + theme.getName() + "` de " + Objects.requireNonNull(guild.getMemberById(ID)).getAsMention(), 1);
        msg.replyEmbeds(embed.build()).queue();
    }
}
