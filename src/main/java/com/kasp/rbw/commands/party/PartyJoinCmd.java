package com.kasp.rbw.commands.party;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
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
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PartyJoinCmd extends Command {
    public PartyJoinCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (PartyCache.getParty(player) != null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("already-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        Player leader = PlayerCache.getPlayer(ID);

        if (PartyCache.getParty(leader) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("player-not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(leader);

        if (!party.getInvitedPlayers().contains(PlayerCache.getPlayer(sender.getId()))) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-invited"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (party.getMembers().size() >= Integer.parseInt(Config.getValue("max-party-members"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("this-party-full"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int partyElo = 0;
        for (Player p : party.getMembers()) {
            partyElo += p.getElo();
        }

        if (partyElo + player.getElo() > Integer.parseInt(Config.getValue("max-party-elo"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", "Você tem muito elo para entrar nessa party\nParty elo: `" + partyElo + "`\nSeu elo: `" + player.getElo() + "`\nLimite de elo: `" + Config.getValue("max-party-elo") + "`", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        player.joinParty(party);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("joined-party"), 1);
        msg.replyEmbeds(reply.build()).queue();

        Embed embed = new Embed(EmbedType.DEFAULT, "", "<@" + player.getID() + "> entrou na party", 1);
        channel.sendMessage("<@" + leader.getID() + ">").setEmbeds(embed.build()).queue();
    }
}