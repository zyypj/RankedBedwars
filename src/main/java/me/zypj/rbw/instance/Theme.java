package me.zypj.rbw.instance;

import lombok.Getter;
import me.zypj.rbw.instance.cache.ThemeCache;

@Getter
public class Theme {

    private final String name;

    public Theme(String name) {
        this.name = name;

        ThemeCache.initializeTheme(name, this);
    }
}
