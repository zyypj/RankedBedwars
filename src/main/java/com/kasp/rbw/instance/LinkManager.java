package com.kasp.rbw.instance;

import java.util.*;

public class LinkManager {

    private static Map<String, Integer> links = new HashMap<>();

    public static int addPlayer(String ID) {
        if (links.containsKey(ID)) {
            return -1;
        }

        int code = getRandomNumber();

        while (links.containsValue(code)) {
            code = getRandomNumber();
        }

        links.put(ID, code);

        TimerTask task = new TimerTask () {
            @Override
            public void run () {
                links.remove(ID);
            }
        };

        // 5 mins
        new Timer().schedule(task, 1000 * 60 * 5);

        return code;
    }

    public static void removePlayer(int code) {
        if (!links.containsValue(code)) {
            return;
        }

        String ID = null;

        for (String s : links.keySet()) {
            if (links.get(s).equals(code)) {
                ID = s;
            }
        }

        if (ID != null) {
            links.remove(ID);
        }
    }

    public static String getMemberByCode(int code) {
        if (!links.containsValue(code)) {
            return null;
        }

        String ID = null;

        for (String s : links.keySet()) {
            if (links.get(s).equals(code)) {
                ID = s;
            }
        }

        return ID;
    }

    public static boolean isValidCode(int code) {
        return links.containsValue(code);
    }

    public static int getRandomNumber() {
        return new Random().nextInt(900000) + 100000;
    }
}
