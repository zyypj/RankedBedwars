package me.zypj.rbw.instance;

import lombok.Getter;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.instance.cache.LevelCache;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Getter
public class Level {

    private final int level;
    private final int neededXP;
    private final List<String> rewards;

    public Level(int level) {
        this.level = level;
        this.rewards = new ArrayList<>();

        if (level == 0) {
            this.neededXP = 0;
        } else {
            Yaml yaml = new Yaml();
            String path = RBWPlugin.getInstance().getDataFolder() + "/RankedBot/levels.yml";
            try (InputStream in = new FileInputStream(path)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = yaml.loadAs(in, Map.class);

                String levelData = Objects.requireNonNull(data.get("l" + level)).toString();

                if (levelData.contains(";;;")) {
                    String[] parts = levelData.split(";;;");
                    this.neededXP = Integer.parseInt(parts[0]);

                    String rewardsPart = parts[1];
                    if (rewardsPart.contains(",")) {
                        this.rewards.addAll(Arrays.asList(rewardsPart.split(",")));
                    } else {
                        this.rewards.add(rewardsPart);
                    }
                } else {
                    this.neededXP = Integer.parseInt(levelData);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error when reading levels.yml", e);
            }
        }

        LevelCache.initializeLevel(level, this);
    }
}
