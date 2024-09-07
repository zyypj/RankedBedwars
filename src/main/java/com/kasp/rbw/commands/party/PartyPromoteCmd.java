package com.kasp.rbw.commands.party;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Party;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PartyCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PartyPromoteCmd extends Command {
    public PartyPromoteCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        if (PartyCache.getParty(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(player);

        if (party.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-party-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        Player promoted = PlayerCache.getPlayer(ID);

        if (!party.getInvitedPlayers().contains(promoted)) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("player-not-in-your-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        party.promote(promoted);

        Embed reply = new Embed(EmbedType.SUCCESS, "", "Jogador <@" + ID + "> foi promovido a dono da party", 1);
        msg.replyEmbeds(reply.build()).queue();

        Embed embed = new Embed(EmbedType.DEFAULT, "", "Você foi promovido a dono da party de <@" + sender.getId() + ">", 1);
        channel.sendMessage("<@" + ID + ">").setEmbeds(embed.build()).queue();
    }
}
