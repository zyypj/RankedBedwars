package me.zypj.rbw.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.instance.Player;
import me.zypj.rbw.instance.cache.PlayerCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "rbw";
    }

    @Override
    public @NotNull String getAuthor() {
        return "tadeu";
    }

    @Override
    public @NotNull String getVersion() {
        return RBWPlugin.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(org.bukkit.entity.Player player, @NotNull String params) {
        Player member = PlayerCache.getPlayerByIgn(player.getName());

        switch (params) {
            case "elo":
                return String.valueOf(member.getElo());
            case "kills":
                return String.valueOf(member.getKills());
            case "deaths":
                return String.valueOf(member.getDeaths());
            case "gold":
                return String.valueOf(member.getGold());
            case "id":
                return member.getID();
            default:
                return null;
        }
    }
}
