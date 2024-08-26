package com.kasp.rbw.instance;

import com.kasp.rbw.instance.cache.ThemeCache;

public class Theme {

    private String name;

    public Theme(String name) {
        this.name = name;

        ThemeCache.initializeTheme(name, this);
    }

    public String getName() {
        return name;
    }
}
