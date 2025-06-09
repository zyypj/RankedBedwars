package me.zypj.rbw.commands.utilities;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Level;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.LevelCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
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
            msg.replyEmbeds(new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1).build()).queue();
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
