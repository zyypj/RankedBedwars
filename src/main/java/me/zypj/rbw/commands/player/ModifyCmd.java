package me.zypj.rbw.commands.player;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.Statistic;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.LevelCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ModifyCmd extends Command {
    public ModifyCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 4) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]","");
        Statistic stat;

        try {
            stat = Statistic.valueOf(args[2].toUpperCase());
        } catch (Exception e) {
            StringBuilder stats = new StringBuilder();
            for (Statistic s : Statistic.values()) {
                stats.append("`").append(s).append("` ");
            }
            Embed embed = new Embed(EmbedType.ERROR, "Error", "**This statistic does not exist**\n" +
                    "Available: " + stats, 1);
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        int amount = Integer.parseInt(args[3]);

        Player player = PlayerCache.getPlayer(ID);

        Embed embed = new Embed(EmbedType.SUCCESS, "`" + player.getIgn() + "`'s stats modified", "", 1);

        switch (stat) {
            case ELO:
                player.setElo(player.getElo() + amount);
                embed.setDescription("Elo: " + (player.getElo() - amount) + " > " + player.getElo());
                break;
            case WINS:
                player.setWins(player.getWins() + amount);
                embed.setDescription("Wins: " + (player.getWins() - amount) + " > " + player.getWins());
                break;
            case LOSSES:
                player.setLosses(player.getLosses() + amount);
                embed.setDescription("Defeats: " + (player.getLosses() - amount) + " > " + player.getLosses());
                break;
            case MVP:
                player.setMvp(player.getMvp() + amount);
                embed.setDescription("MVP: " + (player.getMvp() - amount) + " > " + player.getMvp());
                break;
            case KILLS:
                player.setKills(player.getKills() + amount);
                embed.setDescription("Kills: " + (player.getKills() - amount) + " > " + player.getKills());
                break;
            case DEATHS:
                player.setDeaths(player.getDeaths() + amount);
                embed.setDescription("Deaths: " + (player.getDeaths() - amount) + " > " + player.getDeaths());
                break;
            case STRIKES:
                player.setStrikes(player.getStrikes() + amount);
                embed.setDescription("Strikes: " + (player.getStrikes() - amount) + " > " + player.getStrikes());
                break;
            case SCORED:
                player.setScored(player.getScored() + amount);
                embed.setDescription("Scores: " + (player.getScored() - amount) + " > " + player.getScored());
                break;
            case GOLD:
                player.setGold(player.getGold() + amount);
                embed.setDescription("Golds: " + (player.getGold() - amount) + " > " + player.getGold());
                break;
            case LEVEL:
                player.setLevel(LevelCache.getLevel(player.getLevel().getLevel() + amount));
                embed.setDescription("Level: " + (player.getLevel().getLevel() - amount) + " > " + player.getLevel().getLevel());
                break;
            case XP:
                player.setXp(player.getXp() + amount);
                embed.setDescription("XP: " + (player.getXp() - amount) + " > " + player.getXp());
                break;
            default:
                Embed error = new Embed(EmbedType.ERROR, "Error", "You can't do that", 1);
                msg.replyEmbeds(error.build()).queue();
                return;
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
