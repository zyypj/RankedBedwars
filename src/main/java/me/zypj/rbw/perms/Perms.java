package me.zypj.rbw.perms;

import me.zypj.rbw.RBWPlugin;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Perms {

    public static HashMap<String, String> permsData = new HashMap<>();

    public static void loadPerms() {
        String filename = "permissions.yml";
        ClassLoader classLoader = RBWPlugin.class.getClassLoader();

        try (InputStream resourceStream = classLoader.getResourceAsStream(filename)) {
            if (resourceStream == null) {
                throw new RuntimeException("Resource not found: " + filename);
            }
            String defaultContent = IOUtils.toString(resourceStream, StandardCharsets.UTF_8);

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
                    if (entry.getValue() != null) {
                        permsData.put(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().getConsoleSender().sendMessage("§aSuccessfully loaded the permissions file into memory");
    }

    public static void reload() {
        permsData.clear();
        loadPerms();
    }

    public static String getPerm(String permission) {
        return permsData.get(permission);
    }
}
