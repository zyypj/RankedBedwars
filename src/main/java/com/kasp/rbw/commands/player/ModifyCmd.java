package com.kasp.rbw.commands.player;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.Statistic;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.LevelCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ModifyCmd extends Command {
    public ModifyCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 4) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]","");
        Statistic stat;

        try {
            stat = Statistic.valueOf(args[2].toUpperCase());
        } catch (Exception e) {
            String stats = "";
            for (Statistic s : Statistic.values()) {
                stats += "`" + s + "` ";
            }
            Embed embed = new Embed(EmbedType.ERROR, "Erro", "**Essa estatística não existe**\nDisponíveis: " + stats, 1);
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        int amount = Integer.parseInt(args[3]);

        Player player = PlayerCache.getPlayer(ID);

        Embed embed = new Embed(EmbedType.SUCCESS, "Estatísticas de `" + player.getIgn() + "` Modificadas", "", 1);

        switch (stat) {
            case ELO:
                player.setElo(player.getElo() + amount);
                embed.setDescription("Elo: " + (player.getElo() - amount) + " > " + player.getElo());
                break;
            case WINS:
                player.setWins(player.getWins() + amount);
                embed.setDescription("Vitórias: " + (player.getWins() - amount) + " > " + player.getWins());
                break;
            case LOSSES:
                player.setLosses(player.getLosses() + amount);
                embed.setDescription("Derrotas: " + (player.getLosses() - amount) + " > " + player.getLosses());
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
                embed.setDescription("Mortes: " + (player.getDeaths() - amount) + " > " + player.getDeaths());
                break;
            case STRIKES:
                player.setStrikes(player.getStrikes() + amount);
                embed.setDescription("Strikes: " + (player.getStrikes() - amount) + " > " + player.getStrikes());
                break;
            case SCORED:
                player.setScored(player.getScored() + amount);
                embed.setDescription("Pontuações: " + (player.getScored() - amount) + " > " + player.getScored());
                break;
            case GOLD:
                player.setGold(player.getGold() + amount);
                embed.setDescription("Ouros: " + (player.getGold() - amount) + " > " + player.getGold());
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
                Embed error = new Embed(EmbedType.ERROR, "Erro", "Você não pode fazer isso", 1);
                msg.replyEmbeds(error.build()).queue();
                return;
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
