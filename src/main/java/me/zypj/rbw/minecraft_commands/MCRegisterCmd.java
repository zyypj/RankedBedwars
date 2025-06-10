package me.zypj.rbw.minecraft_commands;

import me.zypj.rbw.sample.EmbedType;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.config.Config;
import me.zypj.rbw.database.SQLPlayerManager;
import me.zypj.rbw.instance.Embed;
import me.zypj.rbw.instance.LinkManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MCRegisterCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Use: /register <code>");

            return false;
        }

        int code = 0;
        try {
            code = Integer.parseInt(args[0]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "The code has 6 " + ChatColor.UNDERLINE + "numbers");
            return false;
        }

        if (code == 0) {
            player.sendMessage(ChatColor.RED + "Something went wrong.. Try again later");
            return false;
        }

        if (!LinkManager.isValidCode(code)) {
            player.sendMessage(ChatColor.RED + "The code is invalid. Use =register again");
            return false;
        }

        Member member = null;
        try {
            member = RBWPlugin.guild.getMemberById(Objects.requireNonNull(LinkManager.getMemberByCode(code)));
        } catch (Exception ignored) {}

        if (member == null) {
            player.sendMessage(ChatColor.RED + "Something went wrong.. Try again later");
            return false;
        }

        if (me.zypj.rbw.instance.Player.isRegistered(member.getId())) {
            player.sendMessage(ChatColor.RED + "You are already registered\nUse /rename to change your name");
            return false;
        }

        SQLPlayerManager.createPlayer(member.getId(), sender.getName());
        me.zypj.rbw.instance.Player plr = new me.zypj.rbw.instance.Player(member.getId());
        plr.fix();

        LinkManager.removePlayer(code);

        player.sendMessage(ChatColor.GREEN + "Linked to " + ChatColor.DARK_GREEN + member.getUser().getAsTag());

        TextChannel alerts = RBWPlugin.guild.getTextChannelById(Config.getValue("alerts-channel"));
        Embed embed = new Embed(EmbedType.SUCCESS, "Linking Completed", "You have been registered as `" + player.getName() + "`", 1);
        assert alerts != null;
        alerts.sendMessage("<@" + member.getId() + ">").setEmbeds(embed.build()).queue();

        return true;
    }
}
