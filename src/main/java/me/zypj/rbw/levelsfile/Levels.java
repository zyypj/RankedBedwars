package me.zypj.rbw.levelsfile;

import me.zypj.rbw.RBWPlugin;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
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
        ClassLoader classLoader = RBWPlugin.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + filename);
            }
            String defaultContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            File file = new File(RBWPlugin.getInstance().getDataFolder(), "RankedBot/" + filename);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(defaultContent);
                }
            }

            Yaml yaml = new Yaml();
            try (InputStream fis = new FileInputStream(file)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = yaml.loadAs(fis, Map.class);
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    levelsData.put(entry.getKey(), entry.getValue().toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().getConsoleSender().sendMessage("Successfully loaded the levels file into memory");
    }

    public static String getLevel(String key) {
        return levelsData.get(key);
    }

    public static void loadClanLevels() {
        String filename = "clanlevels.yml";
        ClassLoader classLoader = RBWPlugin.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + filename);
            }
            String defaultContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            File file = new File(RBWPlugin.getInstance().getDataFolder(), "RankedBot/" + filename);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(defaultContent);
                }
            }

            Yaml yaml = new Yaml();
            try (InputStream fis = new FileInputStream(file)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = yaml.loadAs(fis, Map.class);
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    clanLevelsData.put(entry.getKey(), entry.getValue().toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().getConsoleSender().sendMessage("Successfully loaded the clan levels file into memory");
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
