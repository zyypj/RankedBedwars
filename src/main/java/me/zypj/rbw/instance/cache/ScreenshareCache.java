package me.zypj.rbw.instance.cache;

import me.zypj.rbw.config.Config;
import me.zypj.rbw.instance.ScreenShare;
import lombok.Getter;

import java.util.HashMap;

public class ScreenshareCache {

    // channelID, screenshare
    @Getter
    private static final HashMap<String, ScreenShare> screenshares = new HashMap<>();

    public static ScreenShare getScreenshare(String ID) {
        return screenshares.get(ID);
    }

    public static void addScreenshare(ScreenShare ss) {
        screenshares.put(ss.getChannelID(), ss);

        Config.debug("Screenshare for " + ss.getTarget().getIgn() + " has loaded");
    }

    public static void removeScreenshare(String ID) {
        screenshares.remove(ID);
    }

    public static boolean containsScreenshare(ScreenShare ss) {
        return screenshares.containsValue(ss);
    }

    public static void initializeScreenshare(ScreenShare ss) {
        if (!containsScreenshare(ss))
            addScreenshare(ss);
    }

}
