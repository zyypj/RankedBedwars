package me.zypj.rbw.commands.server;

import me.zypj.rbw.commands.Command;
import me.zypj.rbw.commands.CommandManager;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.messages.Msg;
import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.sample.EmbedType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.Objects;

public class HelpCmd extends Command {

    public HelpCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            msg.replyEmbeds(
                    new Embed(
                            EmbedType.ERROR,
                            "Invalid Arguments",
                            Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()),
                            1
                    ).build()
            ).queue();
            return;
        }

        ArrayList<Command> commands = new ArrayList<>(CommandManager.getAllCommands());

        // Show top-level help
        if (args.length == 1) {
            Embed reply = new Embed(
                    EmbedType.DEFAULT,
                    "Help",
                    "Use `=help <subsystem>` to view help for a specific subsystem",
                    1
            );

            for (CommandSubsystem s : CommandSubsystem.values()) {
                String name = s.toString().toLowerCase();
                reply.addField(
                        "• " + name + " subsystem",
                        "Use `=help " + name + "`",
                        false
                );
            }

            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        // Show detailed help for one subsystem
        if (args.length == 2) {
            CommandSubsystem subsystem;
            try {
                subsystem = CommandSubsystem.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                StringBuilder subsystems = new StringBuilder();
                for (CommandSubsystem s : CommandSubsystem.values()) {
                    subsystems.append("`").append(s).append("` ");
                }
                Embed embed = new Embed(
                        EmbedType.ERROR,
                        "Error",
                        "That subsystem does not exist\nAvailable subsystems: " + subsystems,
                        1
                );
                msg.replyEmbeds(embed.build()).queue();
                return;
            }

            ArrayList<Command> subsystemCmds = new ArrayList<>();
            for (Command cmd : commands) {
                if (cmd.getSubsystem() == subsystem) {
                    subsystemCmds.add(cmd);
                }
            }

            // Send a placeholder "Loading..." message
            Message embedMsg = msg.replyEmbeds(
                    new EmbedBuilder().setTitle("Loading...").build()
            ).complete();

            // Paginate commands, 3 per page
            for (int j = 0; j < subsystemCmds.size(); j += 3) {
                int totalPages = (int) Math.ceil(subsystemCmds.size() / 3.0);
                Embed reply = new Embed(
                        EmbedType.DEFAULT,
                        "All commands: " + subsystem,
                        "",
                        totalPages
                );

                for (int i = 0; i < 3; i++) {
                    if (i + j < subsystemCmds.size()) {
                        Command cmd = subsystemCmds.get(i + j);

                        StringBuilder aliases = new StringBuilder();
                        for (String a : cmd.getAliases()) {
                            aliases.append("`").append(a).append("` ");
                        }

                        StringBuilder permissions = new StringBuilder();
                        for (String p : cmd.getPermissions()) {
                            if ("everyone".equals(p)) {
                                permissions = new StringBuilder("@everyone");
                            } else {
                                permissions.append(
                                        Objects.requireNonNull(guild.getRoleById(p)).getAsMention()
                                );
                            }
                        }

                        reply.addField(
                                "• " + cmd.getCommand(),
                                cmd.getDescription()
                                        + "\n> Usage: `" + Config.getValue("prefix") + cmd.getUsage() + "`"
                                        + "\n> Aliases: " + aliases
                                        + "\n> Permissions: " + permissions + "\n",
                                false
                        );
                    }
                }

                reply.addField(
                        "Note",
                        "`<something>` - required\n`[something]` - optional",
                        false
                );

                if (j == 0) {
                    embedMsg.editMessageEmbeds(reply.build())
                            .setActionRow(Embed.createButtons(reply.getCurrentPage()))
                            .queue();
                }

                Embed.addPage(embedMsg.getId(), reply);
            }
        }
    }
}
