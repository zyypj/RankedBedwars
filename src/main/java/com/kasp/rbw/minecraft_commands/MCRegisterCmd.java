package com.kasp.rbw.minecraft_commands;

import com.kasp.rbw.sample.EmbedType;
import com.kasp.rbw.RBW;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.database.SQLPlayerManager;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.LinkManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            player.sendMessage(ChatColor.RED + "O código correto tem 6 " + ChatColor.UNDERLINE + "números");
            return false;
        }

        if (code == 0) {
            player.sendMessage(ChatColor.RED + "Algo deu errado.. Tente novamente mais tarde");
            return false;
        }

        if (!LinkManager.isValidCode(code)) {
            player.sendMessage(ChatColor.RED + "O código é inválido. Por favor use =register novamente");
            return false;
        }

        Member member = null;
        try {
            member = RBW.guild.getMemberById(LinkManager.getMemberByCode(code));
        } catch (Exception ignored) {}

        if (member == null) {
            player.sendMessage(ChatColor.RED + "Algo deu errado.. Tente novamente mais tarde");
            return false;
        }

        if (com.kasp.rbw.instance.Player.isRegistered(member.getId())) {
            player.sendMessage(ChatColor.RED + "Você já está registrado\nUse /rename para mudar seu nome");
            return false;
        }

        SQLPlayerManager.createPlayer(member.getId(), sender.getName());
        com.kasp.rbw.instance.Player plr = new com.kasp.rbw.instance.Player(member.getId());
        plr.fix();

        LinkManager.removePlayer(code);

        player.sendMessage(ChatColor.GREEN + "Vinculado a " + ChatColor.DARK_GREEN + member.getUser().getAsTag());

        TextChannel alerts = RBW.guild.getTextChannelById(Config.getValue("alerts-channel"));
        Embed embed = new Embed(EmbedType.SUCCESS, "Vinculação Concluida", "Você foi registrado como `" + player.getName() + "`", 1);
        alerts.sendMessage("<@" + member.getId() + ">").setEmbeds(embed.build()).queue();

        return true;
    }
}
