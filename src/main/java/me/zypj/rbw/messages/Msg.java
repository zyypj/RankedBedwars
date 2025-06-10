package me.zypj.rbw.messages;

import me.zypj.rbw.RBWPlugin;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Msg {

    public static HashMap<String, String> msgData = new HashMap<>();

    public static void loadMsg() {
        String filename = "messages.yml";
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
                    msgData.put(entry.getKey(), entry.getValue().toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getServer().getConsoleSender().sendMessage("Successfully loaded the messages file into memory");
    }

    public static void reload() {
        msgData.clear();
        loadMsg();
    }

    public static String getMsg(String key) {
        return msgData.get(key);
    }
}
