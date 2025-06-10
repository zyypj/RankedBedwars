package me.zypj.rbw.minecraft_commands;

import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.instance.LinkManager;
import me.zypj.rbw.instance.cache.PlayerCache;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MCRenameCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Use: /rename <code>");

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
            player.sendMessage(ChatColor.RED + "The code is invalid. Use =rename again");
            return false;
        }

        Member member = null;
        try {
            member = RBWPlugin.guild.getMemberById(Objects.requireNonNull(LinkManager.getMemberByCode(code)));
        } catch (Exception ignored) {
        }

        if (member == null) {
            player.sendMessage(ChatColor.RED + "Something went wrong.. Try again later");
            return false;
        }

        me.zypj.rbw.instance.Player plr = PlayerCache.getPlayer(member.getId());
        plr.setIgn(sender.getName());
        plr.fix();

        LinkManager.removePlayer(code);

        player.sendMessage(ChatColor.GREEN + "Renamed to " + ChatColor.DARK_GREEN + "(" + member.getUser().getAsTag() + ")");

        return true;
    }
}
