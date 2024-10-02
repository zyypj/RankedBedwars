package com.kasp.rbw.commands.server;

import com.kasp.rbw.sample.CommandSubsystem;
import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.commands.CommandManager;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.messages.Msg;
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
            msg.replyEmbeds(new Embed(EmbedType.ERROR, "Argumentos Inválidos", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1).build()).queue();
            return;
        }

        ArrayList<Command> commands = new ArrayList<>(CommandManager.getAllCommands());

        if (args.length == 1) {
            Embed reply = new Embed(EmbedType.DEFAULT, "Ajuda", "Use `=help <subsystem>` para ver a ajuda de algum SubSistema", 1);

            for (CommandSubsystem s : CommandSubsystem.values()) {
                reply.addField("• " + s.toString().toLowerCase() + " sub-system", "use `=help " + s.toString().toLowerCase() + "`", false);
            }

            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (args.length == 2) {
            CommandSubsystem subsystem;

            try {
                subsystem = CommandSubsystem.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                StringBuilder subsystems = new StringBuilder();
                for (CommandSubsystem s :CommandSubsystem.values()) {
                    subsystems.append("`").append(s).append("` ");
                }
                Embed embed = new Embed(EmbedType.ERROR, "Erro", "Esse SubSistema não existe\nSubsistemas Disponíveis: " + subsystems, 1);
                msg.replyEmbeds(embed.build()).queue();
                return;
            }

            ArrayList<Command> subsystemCmds = new ArrayList<>();

            for (Command cmd : commands) {
                if (cmd.getSubsystem() == subsystem)
                    subsystemCmds.add(cmd);
            }

            Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("Carregando...").build()).complete();

            for (int j = 0; j < (double) subsystemCmds.size(); j+=3) {

                Embed reply = new Embed(EmbedType.DEFAULT, "Todos os comandos: " + subsystem, "", (int) Math.ceil(subsystemCmds.size() / 3.0));

                for (int i = 0; i < 3; i++) {
                    if (i + j < subsystemCmds.size()) {

                        StringBuilder aliases = new StringBuilder();
                        StringBuilder permissions = new StringBuilder();

                        for (String s : subsystemCmds.get(i + j).getAliases())
                            aliases.append("`").append(s).append("` ");

                        for (String s : subsystemCmds.get(i + j).getPermissions()) {
                            if (s.equals("everyone"))
                                permissions = new StringBuilder("@everyone");
                            else
                                permissions.append(Objects.requireNonNull(guild.getRoleById(s)).getAsMention());
                        }

                        reply.addField("• " + subsystemCmds.get(i + j).getCommand(), subsystemCmds.get(i + j).getDescription() +
                                "\n> Uso: `" + Config.getValue("prefix") + subsystemCmds.get(i + j).getUsage() +
                                "`\n> Atalho: " + aliases +
                                "\n> Permissões: " + permissions + "\n", false);
                    }
                }
                reply.addField("Nota", "`<something>` - obrigatório\n`[something]` - opcional", false);

                if (j == 0) {
                    embedmsg.editMessageEmbeds(reply.build()).setActionRow(Embed.createButtons(reply.getCurrentPage())).queue();
                }

                Embed.addPage(embedmsg.getId(), reply);
            }
        }
    }
}
