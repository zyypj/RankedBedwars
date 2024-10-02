package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Level;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.LevelCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class LevelsCmd extends Command {
    public LevelsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            msg.replyEmbeds(new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1).build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Levels e Info", "", 1);
        StringBuilder levels = new StringBuilder();
        for (Level l : LevelCache.getLevels().values()) {
            if (l.getLevel() != 0) {
                StringBuilder rewards = new StringBuilder();
                for (String s : l.getRewards()) {
                    if (s.startsWith("GOLD")) {
                        rewards.append(s.split("=")[1]).append(" Ouro ");
                    }
                }
                if (rewards.toString().isEmpty()) {
                    levels.append("**").append(l.getLevel()).append("** — XP Necessário: `").append(l.getNeededXP()).append("`\n");
                }
                else {
                    levels.append("**").append(l.getLevel()).append("** — XP Necessário: `").append(l.getNeededXP()).append("` Recompensas: `").append(rewards).append("`\n");
                }

            }
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        embed.addField("Seu level", player.getLevel().getLevel() + " `(" + player.getXp() + "/" + LevelCache.getLevel(player.getLevel().getLevel() + 1).getNeededXP() + " XP)`", false);
        embed.addField("Todos levels", levels.toString(), false);
        msg.replyEmbeds(embed.build()).queue();
    }
}
