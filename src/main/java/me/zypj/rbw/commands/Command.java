package me.zypj.rbw.commands;

import me.zypj.rbw.sample.CommandSubsystem;
import me.zypj.rbw.perms.Perms;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Getter
public abstract class Command{

    private final String command;
    private final String usage;
    private final String[] aliases;
    private final String description;
    private final CommandSubsystem subsystem;

    public Command (String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        System.out.println(command + " command successfully loaded");
        this.command = command;
        this.usage = usage;
        this.aliases = aliases;
        this.description = description;
        this.subsystem = subsystem;
    }

    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        System.out.println("Something went wrong...");
    }

    public String[] getPermissions() {
        String permString = Perms.getPerm(this.getCommand());

        if (permString == null) {
            return new String[]{"everyone"};
        }

        return permString.split(",");
    };
}
