package me.zypj.rbw.instance.cache;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.Theme;

import java.util.HashMap;
import java.util.Map;

public class ThemeCache {

    private static final HashMap<String, Theme> themes = new HashMap<>();

    public static Theme getTheme(String name) {
        return themes.get(name);
    }

    public static void addTheme(Theme theme) {
        themes.put(theme.getName(), theme);

        Config.debug("Theme " + theme.getName() + " foi carregado na memoria");
    }

    public static void removeTheme(Theme theme) {
        themes.remove(theme.getName());
    }

    public static boolean containsTheme(String name) {
        return themes.containsKey(name);
    }

    public static void initializeTheme(String name, Theme theme) {
        if (!containsTheme(name))
            addTheme(theme);
    }

    public static Map<String, Theme> getThemes() {
        return themes;
    }
}
