package com.kasp.rbw.commands.clan;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.RBW;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Clan;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.ClanCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;

public class ClanSettingsCmd extends Command {
    public ClanSettingsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        String[] settings = {"privado", "eloreq", "descrição", "icon", "tema"};
        String[] settingsvalue = {"true/false", "number", "text", "attached 135x135 image", "attached 960x540 image"};
        String[] settingsdesc = {"fazer seu clan privado - apenas jogadores convidados irão entrar\nou público - qualquer um entrará no clan",
                                "mudar o elo min. pedido para netrar no clan - para funcionar, o clan deve ser público",
                                "mudar a descrição do clan",
                                "mudar o icone do clan",
                                "mudar o tema do comando =cstats do seu clan"};

        if (args.length < 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        if (ClanCache.getClan(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-in-clan"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(player);

        if (clan.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("not-clan-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String setting = args[1];

        if (!Arrays.asList(settings).contains(setting)) {
            Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("invalid-setting"), 1);
            msg.replyEmbeds(reply.build()).queue();
            Embed embed = new Embed(EmbedType.DEFAULT, "Configurações Disponíveis", "", 1);
            for (int i = 0; i < settings.length; i++) {
                embed.addField(settings[i], "Valor - `" + settingsvalue[i] + "`\n" + settingsdesc[i], false);
            }
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        String value = "";

        int settingIndex = Arrays.asList(settings).indexOf(setting);

        try {
            switch (setting) {
                case "privado":
                    value = args[2];
                    clan.setPrivate(Boolean.parseBoolean(args[2]));
                    break;
                case "eloreq":
                    value = args[2];
                    clan.setEloJoinReq(Integer.parseInt(args[2]));
                    break;
                case "descrição":
                    if (msg.getContentRaw().replace(args[1], "").replace(args[0], "").trim().length() > Integer.parseInt(Config.getValue("clan-desc-max"))) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", Msg.getMsg("desc-too-long"), 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    clan.setDescription(msg.getContentRaw().replace(args[1], "").replace(args[0], "").trim());
                    value = msg.getContentRaw().replace(args[1], "").replace(args[0], "").trim();
                    break;
                case "icon":
                    if (clan.getLevel().getLevel() < Integer.parseInt(Config.getValue("allow-setting-icon"))) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Seu clan precisa do level " + Config.getValue("allow-setting-icon") + " para você mudar o icon", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (!msg.getAttachments().get(0).isImage()) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "O arquivo tem que ser uma imagem. (anta)", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (msg.getAttachments().isEmpty()) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Você deve colocar uma imagem `135x135` como seu icone", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (!msg.getAttachments().get(0).getFileName().equals("icon.png")) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Nenomeie a imagem para `icon.png`", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (msg.getAttachments().get(0).getWidth() != 135 || msg.getAttachments().get(0).getHeight() != 135) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Você deve colocar uma imagem `135x135` como seu icone", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }

                    msg.getAttachments().get(0).downloadToFile(RBW.getInstance().getDataFolder() + "/RankedBot/clans/" + clan.getName() + "/" + msg.getAttachments().get(0).getFileName());

                    value = "icon.png";
                    break;
                case "tema":
                    if (clan.getLevel().getLevel() < Integer.parseInt(Config.getValue("allow-setting-theme"))) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Seu clan precisa do level " + Config.getValue("allow-setting-theme") + " para poder alterar o tema", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (!msg.getAttachments().get(0).isImage()) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "O arquivo tem que ser uma imagem. (anta)", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (msg.getAttachments().isEmpty()) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Você deve colocar uma imagem `960x540` como seu tema", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (!msg.getAttachments().get(0).getFileName().equals("theme.png")) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Renomeie a imagem para `theme.png`", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }
                    if (msg.getAttachments().get(0).getWidth() != 960 || msg.getAttachments().get(0).getHeight() != 540) {
                        Embed reply = new Embed(EmbedType.ERROR, "Erro", "Você deve colocar uma imagem `960x540` como seu tema", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }

                    msg.getAttachments().get(0).downloadToFile("RankedBot/clans/" + clan.getName() + "/" + msg.getAttachments().get(0).getFileName());

                    value = "theme.png";
                    break;
            }
        } catch (Exception e) {
            Embed embed = new Embed(EmbedType.ERROR, "Erro", "Algo deu errado... use algumas dessas configurações `" + settingsvalue[settingIndex] + "` como seu valor", 1);
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.SUCCESS, "Configurações Atualizadas", "Você setou o valor `" + value + "` para a configuração `" + setting + "`", 1);
        msg.replyEmbeds(embed.build()).queue();
    }
}
