package me.zypj.rbw.commands;

import me.zypj.rbw.commands.clan.*;
import me.zypj.rbw.commands.clanwar.*;
import me.zypj.rbw.commands.game.*;
import me.zypj.rbw.commands.party.*;
import me.zypj.rbw.commands.player.*;
import me.zypj.rbw.commands.utilities.*;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.commands.moderation.BanCmd;
import me.zypj.rbw.commands.moderation.BanInfoCmd;
import me.zypj.rbw.commands.moderation.StrikeCmd;
import me.zypj.rbw.commands.moderation.UnbanCmd;
import me.zypj.rbw.commands.server.HelpCmd;
import me.zypj.rbw.commands.server.InfoCmd;
import me.zypj.rbw.commands.server.ReloadConfigCmd;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.perms.Perms;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    static ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new HelpCmd("help", "help [sub-sistema]", new String[]{}, "Olhe todos os comandos", CommandSubsystem.SERVER));
        commands.add(new ReloadConfigCmd("reloadconfig", "reloadconfig", new String[]{"reload", "rc"}, "Reload the configs (update values)", CommandSubsystem.SERVER));
        commands.add(new InfoCmd("info", "info", new String[]{"informacoes"}, "Informações Sobre Bot e Servidor", CommandSubsystem.SERVER));

        commands.add(new RegisterCmd("register", "register", new String[]{"registrar"}, "Registre você antes de jogar", CommandSubsystem.PLAYER));
        commands.add(new RenameCmd("rename", "rename", new String[]{"renomear"}, "Mude seu nick in-game", CommandSubsystem.PLAYER));
        commands.add(new FixCmd("fix", "fix [ID/menção]", new String[]{"correct"}, "Atualize seus cargos e apelido", CommandSubsystem.PLAYER));
        commands.add(new ForceRegisterCmd("forceregister", "forceregister <ID/menção> <ign>", new String[]{"freg"}, "Forçar registro", CommandSubsystem.PLAYER));
        commands.add(new ForceRenameCmd("forcerename", "forcerename <ID/menção> <novo ign>", new String[]{"fren"}, "Forçar mudança de nick", CommandSubsystem.PLAYER));
        commands.add(new ForceUnregisterCmd("forceunregister", "forceunregister <ID/menção>", new String[]{"frunreg"}, "Forçar desregistro", CommandSubsystem.PLAYER));
        commands.add(new WipeCmd("wipe", "wipe <ID/menção/\"everyone\">", new String[]{"reset", "resetar"}, "Resetar todos as estatísticas", CommandSubsystem.PLAYER));
        commands.add(new StatsCmd("stats", "stats [ID/menção/\"full\"]", new String[]{"s", "i"}, "Ver as estatísticas de alguem", CommandSubsystem.PLAYER));
        commands.add(new LeaderboardCmd("leaderboard", "leaderboard <estatística>", new String[]{"lb"}, "Leaderboard de uma estatística", CommandSubsystem.PLAYER));
        commands.add(new ModifyCmd("modify", "modify <ID/menção> <estatística> <valor>", new String[]{"edit", "editar"}, "Modificar uma estatística", CommandSubsystem.PLAYER));
        commands.add(new ScreenshareCmd("screenshare", "screenshare <ID/menção> <motivo>", new String[]{"ss"}, "Pedir Screenshare a player", CommandSubsystem.PLAYER));
        commands.add(new TransferGoldCmd("transfergold", "transfergold <ID/menção> <quantidade>", new String[]{"tg"}, "Transferir golds de uma conta a outra", CommandSubsystem.PLAYER));

        commands.add(new PartyCreateCmd("partycreate", "partycreate", new String[]{"pcreate", "partycriar"}, "Criar uma party", CommandSubsystem.PARTY));
        commands.add(new PartyInviteCmd("partyinvite", "partyinvite <ID/menção>", new String[]{"pinvite", "partyinvite"}, "Convidar um jogador a sua party", CommandSubsystem.PARTY));
        commands.add(new PartyJoinCmd("partyjoin", "partyjoin <ID/menção>", new String[]{"pjoin", "partyjoin"}, "Entrar na party de alguem", CommandSubsystem.PARTY));
        commands.add(new PartyLeaveCmd("partyleave", "partyleave", new String[]{"pleave", "partysair"}, "Sair da sua party atual ou desfaze-la", CommandSubsystem.PARTY));
        commands.add(new PartyPromoteCmd("partypromote", "partypromote <ID/menção>", new String[]{"ppromote", "partypromover"}, "Promover um jogador da party", CommandSubsystem.PARTY));
        commands.add(new PartyWarpCmd("partywarp", "partywarp", new String[]{"pwarp"}, "Puxar todos a sua caçç (apenas os que já estiverem em alguma outra call)", CommandSubsystem.PARTY));
        commands.add(new PartyListCmd("partylist", "partylist [ID/menção]", new String[]{"plist"}, "Ver a lista da sua party", CommandSubsystem.PARTY));
        commands.add(new PartyKickCmd("partykick", "partykick <ID/menção>", new String[]{"pkick"}, "Expulsar um jogador da sua party", CommandSubsystem.PARTY));

        commands.add(new RetryCmd("retry", "retry", new String[]{}, "Tente novamente colocar os jogadores em um mapa", CommandSubsystem.GAME));
        commands.add(new QueueCmd("queue", "queue", new String[]{"q"}, "Veja a fila do seu jogo", CommandSubsystem.GAME));
        commands.add(new QueueStatsCmd("queuestats", "queuestats", new String[]{"qs"}, "Veja as estatísticas da fila do seu jogo", CommandSubsystem.GAME));
        commands.add(new GameInfoCmd("gameinfo", "gameinfo <número>", new String[]{"gi"}, "Veja informações sobre um jogo", CommandSubsystem.GAME));
        commands.add(new PickCmd("pick", "pick <ID/menção>", new String[]{"p"}, "Escolha um jogador no seu jogo (se você for um capitão)", CommandSubsystem.GAME));
        commands.add(new VoidCmd("void", "void", new String[]{"cleargame", "clear", "cg"}, "Cancelar um jogo se não puder mais jogá-lo", CommandSubsystem.GAME));
        commands.add(new CallCmd("call", "call <ID/menção>", new String[]{}, "Dar a um jogador acesso para entrar no seu canal de voz", CommandSubsystem.GAME));
        commands.add(new SubmitCmd("submit", "submit", new String[]{}, "Enviar um jogo para pontuação", CommandSubsystem.GAME));
        commands.add(new ScoreCmd("score", "score <número> <equipe>", new String[]{}, "Pontuar um jogo", CommandSubsystem.GAME));
        commands.add(new UndoGameCmd("undogame", "undogame <número>", new String[]{}, "Desfazer a pontuação de um jogo", CommandSubsystem.GAME));
        commands.add(new WinCmd("win", "win <ID/menção>", new String[]{}, "Dar ao jogador especificado +1 vitória e elo (depende da classificação). Este comando deve ser usado APENAS quando '=score' não funcionar ou para fins de teste", CommandSubsystem.GAME));
        commands.add(new LoseCmd("lose", "lose <ID/menção>", new String[]{}, "Dar ao jogador especificado +1 derrota e -elo (depende da classificação). Este comando deve ser usado APENAS quando '=score' não funcionar ou para fins de teste", CommandSubsystem.GAME));
        commands.add(new ForceVoidCmd("forcevoid", "forcevoid [jogo]", new String[]{"fv"}, "Cancelar forçadamente um jogo (comando de staff)", CommandSubsystem.GAME));

        commands.add(new AddQueueCmd("addqueue", "addqueue <ID do canal de voz> <jogadores por equipe> <modo de escolha (AUTOMATIC/CAPTAINS)> <casual (true/false)>", new String[]{"addq"}, "Adicionar uma fila ranqueada/casual", CommandSubsystem.UTILITIES));
        commands.add(new DeleteQueueCmd("deletequeue", "deletequeue <ID do canal de voz>", new String[]{"delq", "delqueue"}, "Excluir uma fila ranqueada/casual", CommandSubsystem.UTILITIES));
        commands.add(new QueuesCmd("queues", "queues", new String[]{}, "Veja informações sobre todas as filas do servidor", CommandSubsystem.UTILITIES));
        commands.add(new AddRankCmd("addrank", "addrank <ID/menção do cargo> <elo inicial> <elo final> <elo por vitória> <elo por derrota> <elo de MVP>", new String[]{"addr"}, "Adicionar uma classificação", CommandSubsystem.UTILITIES));
        commands.add(new DeleteRankCmd("deleterank", "deleterank <ID/menção do cargo>", new String[]{"delr", "delrank"}, "Excluir uma classificação", CommandSubsystem.UTILITIES));
        commands.add(new RanksCmd("ranks", "ranks", new String[]{}, "Veja todas as classificações e informações sobre elas", CommandSubsystem.UTILITIES));
        commands.add(new MapsCmd("maps", "maps", new String[]{}, "Veja todos os mapas e informações sobre eles", CommandSubsystem.UTILITIES));
        commands.add(new GiveThemeCmd("givetheme", "givetheme <ID/menção> <tema>", new String[]{}, "Dar ao jogador especificado acesso a um tema", CommandSubsystem.UTILITIES));
        commands.add(new RemoveThemeCmd("removetheme", "removetheme <ID/menção> <tema>", new String[]{}, "Remover o acesso a um tema do jogador especificado", CommandSubsystem.UTILITIES));
        commands.add(new ThemeCmd("theme", "theme <tema/\"lista\">", new String[]{}, "Selecionar um tema ou usar \"lista\" para ver todos os temas", CommandSubsystem.UTILITIES));
        commands.add(new LevelsCmd("levels", "levels", new String[]{}, "Veja todos os níveis e informações sobre eles", CommandSubsystem.UTILITIES));
        commands.add(new SSCloseCmd("screenshareclose", "screenshareclose <motivo (resultado)>", new String[]{"ssclose"}, "Fechar um canal de screen share", CommandSubsystem.UTILITIES));

        commands.add(new BanCmd("ban", "ban <ID/menção> <tempo> <motivo>", new String[]{}, "Banir um jogador de entrar na fila", CommandSubsystem.MODERATION));
        commands.add(new UnbanCmd("unban", "unban <ID/menção>", new String[]{}, "Desbanir um jogador banido", CommandSubsystem.MODERATION));
        commands.add(new BanInfoCmd("baninfo", "baninfo <ID/menção>", new String[]{}, "Ver informações sobre um banimento específico", CommandSubsystem.MODERATION));
        commands.add(new StrikeCmd("strike", "strike <ID/menção> <motivo>", new String[]{}, "Dar uma penalidade a um jogador - tirar elo e banir da fila (depende de quantas penalidades o jogador já tem)", CommandSubsystem.MODERATION));

        commands.add(new ClanCreateCmd("clancreate", "clancreate <nome>", new String[]{"ccreate"}, "Criar um clã", CommandSubsystem.CLAN));
        commands.add(new ClanDisbandCmd("clandisband", "clandisband", new String[]{"cdisband"}, "Dissolver o clã que você está (se for o líder)", CommandSubsystem.CLAN));
        commands.add(new ClanInviteCmd("claninvite", "claninvite <ID/menção>", new String[]{"cinvite"}, "Convidar um jogador para o seu clã (convites expiram toda vez que o bot é reiniciado)", CommandSubsystem.CLAN));
        commands.add(new ClanJoinCmd("clanjoin", "clanjoin <nome>", new String[]{"cjoin"}, "Entrar em um clã (se for convidado)", CommandSubsystem.CLAN));
        commands.add(new ClanLeaveCmd("clanleave", "clanleave", new String[]{"cleave"}, "Sair do clã que você está atualmente (se não for o líder)", CommandSubsystem.CLAN));
        commands.add(new ClanStatsCmd("clanstats", "clanstats [nome]", new String[]{"cstats"}, "Veja estatísticas/informações sobre um determinado clã", CommandSubsystem.CLAN));
        commands.add(new ClanInfoCmd("claninfo", "claninfo [nome]", new String[]{"cinfo"}, "Veja todas as informações que não aparecem em `=cstats` sobre o seu clã ou o de alguém", CommandSubsystem.CLAN));
        commands.add(new ClanKickCmd("clankick", "clankick <ID/menção>", new String[]{"ckick"}, "Expulsar um jogador do seu clã", CommandSubsystem.CLAN));
        commands.add(new ClanSettingsCmd("clansettings", "clansettings <configuração> <valor>", new String[]{"csettings"}, "Modificar configurações do seu clã", CommandSubsystem.CLAN));
        commands.add(new ClanListCmd("clanlist", "clanlist", new String[]{"clist"}, "Ver uma lista de todos os clãs no servidor", CommandSubsystem.CLAN));
        commands.add(new ClanLBCmd("clanlb", "clanlb", new String[]{"clb", "clanleaderboard", "cleaderboard"}, "Ver o ranking de clãs com maior reputação", CommandSubsystem.CLAN));
        commands.add(new ClanForceDisbandCmd("clanforcedisband", "clanforcedisband <nome>", new String[]{"cfdisband"}, "Dissolver forçadamente um clã", CommandSubsystem.CLAN));

        commands.add(new CWCreateCmd("cwcreate", "cwcreate <jogadores em cada equipe> <mínimo de clãs> <máximo de clãs> <xp por vitória> <ouro por vitória>", new String[]{""}, "Organizar uma guerra de clãs", CommandSubsystem.CLANWAR));
        commands.add(new CWCancelCmd("cwcancel", "cwcancel <número>", new String[]{""}, "Cancelar uma guerra de clãs", CommandSubsystem.CLANWAR));
        commands.add(new CWRegisterCmd("cwregister", "cwregister <IDs/menções>", new String[]{""}, "Registrar sua equipe de clã para a guerra de clãs", CommandSubsystem.CLANWAR));
        commands.add(new CWUnregisterCmd("cwunregister", "cwunregister", new String[]{""}, "Cancelar o registro da sua equipe de clã para a guerra de clãs", CommandSubsystem.CLANWAR));
        commands.add(new CWStartCmd("cwstart", "cwstart", new String[]{""}, "Iniciar a guerra de clãs atual", CommandSubsystem.CLANWAR));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String prefix = Config.getValue("prefix");

        String[] args = event.getMessage().getContentRaw().split(" ");
        Guild g = event.getGuild();
        Member m = event.getMember();
        TextChannel c = (TextChannel) event.getChannel();
        Message msg = event.getMessage();

        if (!msg.getContentRaw().startsWith(prefix)) {
            return;
        }

        if (RBWPlugin.getGuild() == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Bot Starting", "The bot is currently starting... Please wait a few seconds and use this command again", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (!Boolean.parseBoolean(Config.getValue("unregistered-cmd-usage"))) {
            if (!args[0].replace(prefix, "").equalsIgnoreCase("register")) {
                assert m != null;
                if (!Player.isRegistered(m.getId())) {
                    Embed reply = new Embed(EmbedType.ERROR, "Não Registrado", Msg.getMsg("not-registered"), 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
            }
        }

        Command command = null;

        for (Command cmd : commands) {
            String[] aliases = cmd.getAliases();
            boolean isAlias = Arrays.asList(aliases).contains(args[0].toLowerCase().replace(prefix, ""));
            if (args[0].replace(prefix, "").equalsIgnoreCase(cmd.getCommand()) || isAlias) {
                command = cmd;
            }
        }

        if (command == null) {
            Embed embed = new Embed(EmbedType.ERROR, "Comando não encontrado", "Use `=help` para ver todos os comandos", 1);
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        if (!checkPerms(command, m, g)) {
            Embed reply = new Embed(EmbedType.ERROR, "Sem Permissão", Msg.getMsg("no-perms"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (Boolean.parseBoolean(Config.getValue("log-commands"))) {
            assert m != null;
            System.out.println("[RankedBW] " + m.getUser() .getAsTag() + " used " + msg.getContentRaw());
        }

        command.execute(args, g, m, c, msg);
    }

    public static ArrayList<Command> getAllCommands() {
        return commands;
    }

    private boolean checkPerms (Command cmd, Member m, Guild g) {
        boolean access = false;

        if (Perms.getPerm(cmd.getCommand()) == null) {
            return false;
        }
        if (Perms.getPerm(cmd.getCommand()).equals("everyone")) {
            access = true;
        }
        else {
            if (!Perms.getPerm(cmd.getCommand()).isEmpty()) {
                List<Role> roles = new ArrayList<>();
                for (String s : Perms.getPerm(cmd.getCommand()).split(",")) {
                    roles.add(g.getRoleById(s));
                }

                for (Role r : m.getRoles()) {
                    if (roles.contains(r)) {
                        access = true;
                        break;
                    }
                }
            }
        }

        return access;
    }
}
