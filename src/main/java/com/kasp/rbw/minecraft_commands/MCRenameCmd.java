package com.kasp.rbw.minecraft_commands;

import com.kasp.rbw.RBW;
import com.kasp.rbw.instance.LinkManager;
import com.kasp.rbw.instance.cache.PlayerCache;
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
            player.sendMessage(ChatColor.RED + "O código correto tem 6 " + ChatColor.UNDERLINE + "números");
            return false;
        }

        if (code == 0) {
            player.sendMessage(ChatColor.RED + "Algo deu errado.. Tente novamente mais tarde");
            return false;
        }

        if (!LinkManager.isValidCode(code)) {
            player.sendMessage(ChatColor.RED + "O código é invalido. Por favor use =rename novamente");
            return false;
        }

        Member member = null;
        try {
            member = RBW.guild.getMemberById(Objects.requireNonNull(LinkManager.getMemberByCode(code)));
        } catch (Exception ignored) {}

        if (member == null) {
            player.sendMessage(ChatColor.RED + "Algo deu errado.. Tente novamete mais tarde.");
            return false;
        }

        com.kasp.rbw.instance.Player plr = PlayerCache.getPlayer(member.getId());
        plr.setIgn(sender.getName());
        plr.fix();

        LinkManager.removePlayer(code);

        player.sendMessage(ChatColor.GREEN + "Renomeado para " + ChatColor.DARK_GREEN + "(" + member.getUser().getAsTag() + ")");

        return true;
    }
}
