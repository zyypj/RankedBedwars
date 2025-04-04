package com.kasp.rbw.levelsfile;

import com.kasp.rbw.RBW;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Levels {

    public static HashMap<String, String> levelsData = new HashMap<>();
    public static HashMap<String, String> clanLevelsData = new HashMap<>();

    public static void loadLevels() {
        String filename = "levels.yml";
        ClassLoader classLoader = RBW.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            assert inputStream != null;
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            File file = new File(RBW.getInstance().getDataFolder() + "/RankedBot/" + filename);
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(RBW.getInstance().getDataFolder() + "/RankedBot/" + filename));
                bw.write(result);
                bw.close();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new FileInputStream(RBW.getInstance().getDataFolder() + "/RankedBot/" + filename));
            for (String s : data.keySet()) {
                levelsData.put(s, data.get(s).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully loaded the levels file into memory");
    }

    public static String getLevel(String key) {
        return levelsData.get(key);
    }

    public static void loadClanLevels() {
        String filename = "clanlevels.yml";
        ClassLoader classLoader = RBW.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            assert inputStream != null;
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            File file = new File(RBW.getInstance().getDataFolder() + "/RankedBot/" + filename);
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(RBW.getInstance().getDataFolder() + "/RankedBot/" + filename));
                bw.write(result);
                bw.close();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new FileInputStream(RBW.getInstance().getDataFolder() + "/RankedBot/" + filename));
            for (String s : data.keySet()) {
                clanLevelsData.put(s, data.get(s).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully loaded the clan levels file into memory");
    }

    public static String getClanLevel(String key) {
        return clanLevelsData.get(key);
    }

    public static void reload() {
        levelsData.clear();
        clanLevelsData.clear();

        loadLevels();
        loadClanLevels();
    }
}
