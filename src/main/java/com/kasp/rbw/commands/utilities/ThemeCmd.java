package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.Theme;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.instance.cache.ThemeCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ThemeCmd extends Command {
    public ThemeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String name = args[1];

        if (name.equals("list")) {
            String themes = "";
            for (Theme t : ThemeCache.getThemes().values()) {
                themes += "`" + t.getName() + "` ";
            }

            Embed embed = new Embed(EmbedType.DEFAULT, "Todos os Temas `(" + ThemeCache.getThemes().size() + ")`", themes, 1);
            msg.replyEmbeds(embed.build()).queue();
        }
        else {
            if (!ThemeCache.containsTheme(name)) {
                Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("theme-doesnt-exist"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            Theme theme = ThemeCache.getTheme(name);

            Player player = PlayerCache.getPlayer(sender.getId());

            if (!player.getOwnedThemes().contains(theme)) {
                Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("theme-access-denied"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            player.setTheme(theme);

            Embed embed = new Embed(EmbedType.SUCCESS, "", "Você escolheu o tema `" + theme.getName() + "`", 1);
            msg.replyEmbeds(embed.build()).queue();
        }
    }
}
