package me.zypj.rbw.config;

import me.zypj.rbw.RBWPlugin;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static HashMap<String, String> configData = new HashMap<>();

    public static void loadConfig() {
        String filename = "config.yml";
        ClassLoader classLoader = RBWPlugin.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + filename);
            }
            String defaultConfig = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            File file = new File(RBWPlugin.getInstance().getDataFolder(), "RankedBot/" + filename);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(defaultConfig);
                }
            }

            Yaml yaml = new Yaml();
            try (InputStream fis = new FileInputStream(file)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = yaml.loadAs(fis, Map.class);
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getValue() != null) {
                        configData.put(entry.getKey(), entry.getValue().toString());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().getConsoleSender().sendMessage("§aSuccessfully loaded the config file into memory");
    }

    public static void reload() {
        configData.clear();
        loadConfig();
    }

    public static void debug(String message) {
        if ("true".equalsIgnoreCase(getValue("debug"))) {
            Bukkit.getServer().getConsoleSender().sendMessage(message);
        }
    }

    public static String getValue(String key) {
        return configData.get(key);
    }
}
