package com.kasp.rbw.commands.party;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
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
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.List;

public class PartyInviteCmd extends Command {
    public PartyInviteCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (PlayerCache.getPlayer(ID) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Player", Msg.getMsg("invalid-player"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player invited = PlayerCache.getPlayer(ID);

        if (party.getInvitedPlayers().contains(invited)) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("player-already-invited"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        party.invite(invited);

        Embed reply = new Embed(EmbedType.SUCCESS, "", "Jogador <@" + ID + "> foi convidado para sua party. Ele tem `" + Config.getValue("invite-expiration") + "` minutos para aceitar o convite", 1);
        msg.replyEmbeds(reply.build()).queue();

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("rankedbot-pinvitation-" + player.getID() + "=" + invited.getID(), "Aceitar Convite"));

        Embed embed = new Embed(EmbedType.DEFAULT, "", "Você foi convidado para por <@" + sender.getId() + ">\nEscreva `=pjoin " + sender.getId() + "` ou clique no botão abaixo\nEsse convite dura `" + Config.getValue("invite-expiration") + "` minutos", 1);
        channel.sendMessage("<@" + ID + ">").setEmbeds(embed.build()).setActionRow(buttons).queue();
    }
}
