package com.kasp.rbw.commands.game;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.GameState;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Game;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.GameCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class ScoreCmd extends Command {
    public ScoreCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 4) {
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

        if (game.getState() != GameState.SUBMITTED) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-submitted"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String team = args[2];
        String mvpID = args[3].replaceAll("[^0-9]","");

        List<Player> winningTeam;
        List<Player> losingTeam;

        if (team.equals("1")) {
            winningTeam = game.getTeam1();
            losingTeam = game.getTeam2();
        }
        else {
            winningTeam = game.getTeam2();
            losingTeam = game.getTeam1();
        }

        if (args[3].equalsIgnoreCase("none"))
            game.scoreGame(winningTeam, losingTeam, null, sender);
        else
            game.scoreGame(winningTeam, losingTeam, PlayerCache.getPlayer(mvpID), sender);

        Embed reply = new Embed(EmbedType.SUCCESS, "", "You have scored Game`#" + game.getNumber() + "`", 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
