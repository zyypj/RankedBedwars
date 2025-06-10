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
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    static ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new HelpCmd("help", "help [subsystem]", new String[]{}, "View all commands", CommandSubsystem.SERVER));
        commands.add(new ReloadConfigCmd("reloadconfig", "reloadconfig", new String[]{"reload", "rc"}, "Reload the configs (update values)", CommandSubsystem.SERVER));
        commands.add(new InfoCmd("info", "info", new String[]{"informacoes"}, "Bot and server information", CommandSubsystem.SERVER));

        commands.add(new RegisterCmd("register", "register", new String[]{"registrar"}, "Register before playing", CommandSubsystem.PLAYER));
        commands.add(new RenameCmd("rename", "rename", new String[]{"renomear"}, "Change your in-game nickname", CommandSubsystem.PLAYER));
        commands.add(new FixCmd("fix", "fix [ID/mention]", new String[]{"correct"}, "Update your roles and nickname", CommandSubsystem.PLAYER));
        commands.add(new ForceRegisterCmd("forceregister", "forceregister <ID/mention> <ign>", new String[]{"freg"}, "Force registration", CommandSubsystem.PLAYER));
        commands.add(new ForceRenameCmd("forcerename", "forcerename <ID/mention> <new ign>", new String[]{"fren"}, "Force nickname change", CommandSubsystem.PLAYER));
        commands.add(new ForceUnregisterCmd("forceunregister", "forceunregister <ID/mention>", new String[]{"frunreg"}, "Force unregister", CommandSubsystem.PLAYER));
        commands.add(new WipeCmd("wipe", "wipe <ID/mention/\"everyone\">", new String[]{"reset", "resetar"}, "Reset all statistics", CommandSubsystem.PLAYER));
        commands.add(new StatsCmd("stats", "stats [ID/mention/\"full\"]", new String[]{"s", "i"}, "View someone’s statistics", CommandSubsystem.PLAYER));
        commands.add(new LeaderboardCmd("leaderboard", "leaderboard <statistic>", new String[]{"lb"}, "Leaderboard for a statistic", CommandSubsystem.PLAYER));
        commands.add(new ModifyCmd("modify", "modify <ID/mention> <statistic> <value>", new String[]{"edit", "editar"}, "Modify a statistic", CommandSubsystem.PLAYER));
        commands.add(new ScreenshareCmd("screenshare", "screenshare <ID/mention> <reason>", new String[]{"ss"}, "Request a screenshare from a player", CommandSubsystem.PLAYER));
        commands.add(new TransferGoldCmd("transfergold", "transfergold <ID/mention> <amount>", new String[]{"tg"}, "Transfer gold from one account to another", CommandSubsystem.PLAYER));

        commands.add(new PartyCreateCmd("partycreate", "partycreate", new String[]{"pcreate", "partycriar"}, "Create a party", CommandSubsystem.PARTY));
        commands.add(new PartyInviteCmd("partyinvite", "partyinvite <ID/mention>", new String[]{"pinvite", "partyinvite"}, "Invite a player to your party", CommandSubsystem.PARTY));
        commands.add(new PartyJoinCmd("partyjoin", "partyjoin <ID/mention>", new String[]{"pjoin", "partyjoin"}, "Join someone’s party", CommandSubsystem.PARTY));
        commands.add(new PartyLeaveCmd("partyleave", "partyleave", new String[]{"pleave", "partysair"}, "Leave your current party or disband it", CommandSubsystem.PARTY));
        commands.add(new PartyPromoteCmd("partypromote", "partypromote <ID/mention>", new String[]{"ppromote", "partypromover"}, "Promote a player in the party", CommandSubsystem.PARTY));
        commands.add(new PartyWarpCmd("partywarp", "partywarp", new String[]{"pwarp"}, "Pull everyone into your call (only those already in another call)", CommandSubsystem.PARTY));
        commands.add(new PartyListCmd("partylist", "partylist [ID/mention]", new String[]{"plist"}, "View your party list", CommandSubsystem.PARTY));
        commands.add(new PartyKickCmd("partykick", "partykick <ID/mention>", new String[]{"pkick"}, "Kick a player from your party", CommandSubsystem.PARTY));

        commands.add(new RetryCmd("retry", "retry", new String[]{}, "Retry placing players on a map", CommandSubsystem.GAME));
        commands.add(new QueueCmd("queue", "queue", new String[]{"q"}, "View your game queue", CommandSubsystem.GAME));
        commands.add(new QueueStatsCmd("queuestats", "queuestats", new String[]{"qs"}, "View your game queue statistics", CommandSubsystem.GAME));
        commands.add(new GameInfoCmd("gameinfo", "gameinfo <number>", new String[]{"gi"}, "View information about a game", CommandSubsystem.GAME));
        commands.add(new PickCmd("pick", "pick <ID/mention>", new String[]{"p"}, "Pick a player in your game (if you are a captain)", CommandSubsystem.GAME));
        commands.add(new VoidCmd("void", "void", new String[]{"cleargame", "clear", "cg"}, "Cancel a game if you can no longer play it", CommandSubsystem.GAME));
        commands.add(new CallCmd("call", "call <ID/mention>", new String[]{}, "Grant a player access to join your voice channel", CommandSubsystem.GAME));
        commands.add(new SubmitCmd("submit", "submit", new String[]{}, "Submit a game for scoring", CommandSubsystem.GAME));
        commands.add(new ScoreCmd("score", "score <number> <team> <mvp ID/mention/\"none\">", new String[]{}, "Score a game", CommandSubsystem.GAME));
        commands.add(new UndoGameCmd("undogame", "undogame <number>", new String[]{}, "Undo a game's score", CommandSubsystem.GAME));
        commands.add(new WinCmd("win", "win <ID/mention>", new String[]{}, "Give the specified player +1 win and elo (depends on ranking). This command should be used ONLY when '=score' fails or for testing purposes", CommandSubsystem.GAME));
        commands.add(new LoseCmd("lose", "lose <ID/mention>", new String[]{}, "Give the specified player +1 loss and -elo (depends on ranking). This command should be used ONLY when '=score' fails or for testing purposes", CommandSubsystem.GAME));
        commands.add(new ForceVoidCmd("forcevoid", "forcevoid [game]", new String[]{"fv"}, "Force-cancel a game (staff command)", CommandSubsystem.GAME));

        commands.add(new AddQueueCmd("addqueue", "addqueue <voice channel ID> <players per team> <selection mode (AUTOMATIC/CAPTAINS)> <casual (true/false)>", new String[]{"addq"}, "Add a ranked/casual queue", CommandSubsystem.UTILITIES));
        commands.add(new DeleteQueueCmd("deletequeue", "deletequeue <voice channel ID>", new String[]{"delq", "delqueue"}, "Delete a ranked/casual queue", CommandSubsystem.UTILITIES));
        commands.add(new QueuesCmd("queues", "queues", new String[]{}, "View information about all server queues", CommandSubsystem.UTILITIES));
        commands.add(new AddRankCmd("addrank", "addrank <role ID/mention> <starting elo> <ending elo> <elo per win> <elo per loss> <elo for MVP>", new String[]{"addr"}, "Add a rank", CommandSubsystem.UTILITIES));
        commands.add(new DeleteRankCmd("deleterank", "deleterank <role ID/mention>", new String[]{"delr", "delrank"}, "Delete a rank", CommandSubsystem.UTILITIES));
        commands.add(new RanksCmd("ranks", "ranks", new String[]{}, "View all ranks and their information", CommandSubsystem.UTILITIES));
        commands.add(new MapsCmd("maps", "maps", new String[]{}, "View all maps and their information", CommandSubsystem.UTILITIES));
        commands.add(new GiveThemeCmd("givetheme", "givetheme <ID/mention> <theme>", new String[]{}, "Give the specified player access to a theme", CommandSubsystem.UTILITIES));
        commands.add(new RemoveThemeCmd("removetheme", "removetheme <ID/mention> <theme>", new String[]{}, "Remove the specified player's access to a theme", CommandSubsystem.UTILITIES));
        commands.add(new ThemeCmd("theme", "theme <theme/\"list\">", new String[]{}, "Select a theme or use \"list\" to view all themes", CommandSubsystem.UTILITIES));
        commands.add(new LevelsCmd("levels", "levels", new String[]{}, "View all levels and their information", CommandSubsystem.UTILITIES));
        commands.add(new SSCloseCmd("screenshareclose", "screenshareclose <reason (result)>", new String[]{"ssclose"}, "Close a screen share channel", CommandSubsystem.UTILITIES));

        commands.add(new BanCmd("ban", "ban <ID/mention> <duration> <reason>", new String[]{}, "Ban a player from joining the queue", CommandSubsystem.MODERATION));
        commands.add(new UnbanCmd("unban", "unban <ID/mention>", new String[]{}, "Unban a previously banned player", CommandSubsystem.MODERATION));
        commands.add(new BanInfoCmd("baninfo", "baninfo <ID/mention>", new String[]{}, "View information about a specific ban", CommandSubsystem.MODERATION));
        commands.add(new StrikeCmd("strike", "strike <ID/mention> <reason>", new String[]{}, "Issue a penalty to a player – remove elo and ban from the queue (based on how many penalties the player already has)", CommandSubsystem.MODERATION));

        commands.add(new ClanCreateCmd("clancreate", "clancreate <name>", new String[]{"ccreate"}, "Create a clan", CommandSubsystem.CLAN));
        commands.add(new ClanDisbandCmd("clandisband", "clandisband", new String[]{"cdisband"}, "Disband your current clan (if you are the leader)", CommandSubsystem.CLAN));
        commands.add(new ClanInviteCmd("claninvite", "claninvite <ID/mention>", new String[]{"cinvite"}, "Invite a player to your clan (invitations expire every time the bot restarts)", CommandSubsystem.CLAN));
        commands.add(new ClanJoinCmd("clanjoin", "clanjoin <name>", new String[]{"cjoin"}, "Join a clan (if invited)", CommandSubsystem.CLAN));
        commands.add(new ClanLeaveCmd("clanleave", "clanleave", new String[]{"cleave"}, "Leave the clan you are currently in (if you’re not the leader)", CommandSubsystem.CLAN));
        commands.add(new ClanStatsCmd("clanstats", "clanstats [name]", new String[]{"cstats"}, "View statistics/info for a specific clan", CommandSubsystem.CLAN));
        commands.add(new ClanInfoCmd("claninfo", "claninfo [name]", new String[]{"cinfo"}, "View all information not shown in `=cstats` about your clan or someone else’s", CommandSubsystem.CLAN));
        commands.add(new ClanKickCmd("clankick", "clankick <ID/mention>", new String[]{"ckick"}, "Kick a player from your clan", CommandSubsystem.CLAN));
        commands.add(new ClanSettingsCmd("clansettings", "clansettings <setting> <value>", new String[]{"csettings"}, "Modify your clan’s settings", CommandSubsystem.CLAN));
        commands.add(new ClanListCmd("clanlist", "clanlist", new String[]{"clist"}, "View a list of all clans on the server", CommandSubsystem.CLAN));
        commands.add(new ClanLBCmd("clanlb", "clanlb", new String[]{"clb", "clanleaderboard", "cleaderboard"}, "View the leaderboard of clans by reputation", CommandSubsystem.CLAN));
        commands.add(new ClanForceDisbandCmd("clanforcedisband", "clanforcedisband <name>", new String[]{"cfdisband"}, "Force-disband a clan", CommandSubsystem.CLAN));

        commands.add(new CWCreateCmd("cwcreate", "cwcreate <players per team> <minimum clans> <maximum clans> <xp per win> <gold per win>", new String[]{""}, "Organize a clan war", CommandSubsystem.CLANWAR));
        commands.add(new CWCancelCmd("cwcancel", "cwcancel <number>", new String[]{""}, "Cancel a clan war", CommandSubsystem.CLANWAR));
        commands.add(new CWRegisterCmd("cwregister", "cwregister <IDs/mentions>", new String[]{""}, "Register your clan team for the clan war", CommandSubsystem.CLANWAR));
        commands.add(new CWUnregisterCmd("cwunregister", "cwunregister", new String[]{""}, "Unregister your clan team from the clan war", CommandSubsystem.CLANWAR));
        commands.add(new CWStartCmd("cwstart", "cwstart", new String[]{""}, "Start the current clan war", CommandSubsystem.CLANWAR));
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
                    Embed reply = new Embed(EmbedType.ERROR, "Not registered", Msg.getMsg("not-registered"), 1);
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
            Embed embed = new Embed(EmbedType.ERROR, "Command not found", "Use `=help` to see all commands", 1);
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        if (!checkPerms(command, m, g)) {
            Embed reply = new Embed(EmbedType.ERROR, "No permission", Msg.getMsg("no-perms"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (Boolean.parseBoolean(Config.getValue("log-commands"))) {
            assert m != null;
            Bukkit.getServer().getConsoleSender().sendMessage("[RankedBW] " + m.getUser() .getAsTag() + " used " + msg.getContentRaw());
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
