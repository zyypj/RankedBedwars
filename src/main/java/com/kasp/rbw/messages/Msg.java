package com.kasp.rbw.messages;

import com.kasp.rbw.RBW;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Msg {

    public static HashMap<String, String> msgData = new HashMap<>();

    public static void loadMsg() {
        String filename = "messages.yml";
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
            Map<String, Object> data = yaml.load(new FileInputStream(RBW.getInstance().getDataFolder() + "/RankedBot/messages.yml"));
            for (String s : data.keySet()) {
                msgData.put(s, data.get(s).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully loaded the messages file into memory");
    }

    public static void reload() {
        msgData.clear();

        loadMsg();
    }

    public static String getMsg(String msg) {
        return msgData.get(msg);
    }
}
