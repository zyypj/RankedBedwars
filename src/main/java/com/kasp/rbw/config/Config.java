package com.kasp.rbw.config;

import com.kasp.rbw.RBW;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static HashMap<String, String> configData = new HashMap<>();

    public static void loadConfig() {
        String filename = "config.yml";
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
            Map<String, Object> data = yaml.load(new FileInputStream(RBW.getInstance().getDataFolder() + "/RankedBot/config.yml"));
            for (String s : data.keySet()) {
                if (data.get(s) != null) {
                    configData.put(s, data.get(s).toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully loaded the config file into memory");
    }

    public static void reload() {
        configData.clear();

        loadConfig();
    }

    public static void debug(String message) {
        if (Config.getValue("debug").equalsIgnoreCase("true")) {
            System.out.println(message);
        }
    }

    public static String getValue(String key) {
        return configData.get(key);
    }
}
