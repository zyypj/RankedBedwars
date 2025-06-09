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

public class ThemeCmd extends Command {
    public ThemeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String name = args[1];

        if (name.equals("list")) {
            StringBuilder themes = new StringBuilder();
            for (Theme t : ThemeCache.getThemes().values()) {
                themes.append("`").append(t.getName()).append("` ");
            }

            Embed embed = new Embed(EmbedType.DEFAULT, "Todos os Temas `(" + ThemeCache.getThemes().size() + ")`", themes.toString(), 1);
            msg.replyEmbeds(embed.build()).queue();
        }
        else {
            if (!ThemeCache.containsTheme(name)) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("theme-doesnt-exist"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            Theme theme = ThemeCache.getTheme(name);

            Player player = PlayerCache.getPlayer(sender.getId());

            if (!player.getOwnedThemes().contains(theme)) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("theme-access-denied"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            player.setTheme(theme);

            Embed embed = new Embed(EmbedType.SUCCESS, "", "Você escolheu o tema `" + theme.getName() + "`", 1);
            msg.replyEmbeds(embed.build()).queue();
        }
    }
}
