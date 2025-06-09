package me.zypj.rbw.commands.game;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.sample.GameState;
import me.zypj.rbw.commands.Command;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Game;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.GameCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GameInfoCmd extends Command {
    public GameInfoCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int number = Integer.parseInt(args[1]);

        if (GameCache.getGame(number) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-game"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GameCache.getGame(number);

        StringBuilder t1 = new StringBuilder();
        for (Player p : game.getTeam1()) {
            t1.append("• <@").append(p.getID()).append(">\n");
        }

        StringBuilder t2 = new StringBuilder();
        for (Player p : game.getTeam2()) {
            t2.append("• <@").append(p.getID()).append(">\n");
        }

        StringBuilder remaining = new StringBuilder();
        for (Player p : game.getRemainingPlayers()) {
            remaining.append("• <@").append(p.getID()).append(">\n");
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Game `#" + number + "` Info", "estado: `" + game.getState() + "`", 1);
        embed.addField("Team 1", t1.toString(), true);
        embed.addField("Team 2", t2.toString(), true);
        if (remaining.length() > 0) {
            embed.addField("Remaining", remaining.toString(), false);
        }
        embed.addField("Mapa", game.getMap().getName(), true);
        embed.addField("Casual", String.valueOf(game.isCasual()), true);

        if (game.getState() == GameState.VOIDED) {
            embed.addField("Voidado por", "<@" + game.getScoredBy().getId() + ">", true);
        } else if (game.getState() == GameState.SCORED) {
            embed.addField("Scored por", "<@" + game.getScoredBy().getId() + ">", true);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
