package me.zypj.rbw.listener;

import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Party;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PartyCache;
import me.zypj.rbw.instance.cache.PlayerCache;
import me.zypj.rbw.messages.Msg;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class PartyInviteButton extends ListenerAdapter {

    public void onButtonClick(ButtonInteractionEvent event) {
        if (Objects.requireNonNull(Objects.requireNonNull(event.getButton()).getId()).startsWith("rankedbot-pinvitation-")) {
            String[] players = event.getButton().getId().replace("rankedbot-pinvitation-", "").split("=");
            Player leader = PlayerCache.getPlayer(players[0]);
            Player invited = PlayerCache.getPlayer(players[1]);

            if (!Objects.requireNonNull(event.getMember()).getId().equals(invited.getID())) {
                event.reply(Msg.getMsg("not-invited")).setEphemeral(true).queue();
                return;
            }

            if (PartyCache.getParty(invited) != null) {
                event.reply(Msg.getMsg("already-in-party")).setEphemeral(true).queue();
                return;
            }

            if (PartyCache.getParty(leader) == null) {
                event.reply(Msg.getMsg("player-not-in-party")).setEphemeral(true).queue();
                return;
            }

            Party party = PartyCache.getParty(leader);

            if (!party.getInvitedPlayers().contains(invited)) {
                event.reply(Msg.getMsg("not-invited")).setEphemeral(true).queue();
                return;
            }

            if (party.getMembers().size() >= Integer.parseInt(Config.getValue("max-party-members"))) {
                event.reply(Msg.getMsg("this-party-full")).setEphemeral(true).queue();
                return;
            }

            int partyElo = 0;
            for (Player p : party.getMembers()) {
                partyElo += p.getElo();
            }

            if (partyElo + invited.getElo() > Integer.parseInt(Config.getValue("max-party-elo"))) {
                event.reply("Você tem muito elo para entrar nessa party\nParty elo: `" + partyElo + "`\nSeu elo: `" + invited.getElo() + "`\nLimite da Party: `" + Config.getValue("max-party-elo") + "`").setEphemeral(true).queue();
                return;
            }

            invited.joinParty(party);

            Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("joined-party"), 1);
            event.replyEmbeds(reply.build()).setEphemeral(true).queue();

            Embed embed = new Embed(EmbedType.DEFAULT, "", "<@" + invited.getID() + "> entrou na party", 1);
            event.getChannel().sendMessage("<@" + leader.getID() + ">").setEmbeds(embed.build()).queue();
        }
    }
}
