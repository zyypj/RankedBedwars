package me.zypj.rbw.instance;

import lombok.Getter;
import me.zypj.rbw.RBWPlugin;
import me.zypj.rbw.instance.cache.ClanLevelCache;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Getter
public class ClanLevel {

    private final int level;
    private final int neededXP;

    public ClanLevel(int level) {
        this.level = level;

        if (level == 0) {
            this.neededXP = 0;
        } else {
            Yaml yaml = new Yaml();
            String path = RBWPlugin.getInstance().getDataFolder() + "/RankedBot/clanlevels.yml";
            try (InputStream in = new FileInputStream(path)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = yaml.loadAs(in, Map.class);

                String value = Objects.requireNonNull(data.get("l" + level)).toString();
                this.neededXP = Integer.parseInt(value);
            } catch (IOException e) {
                throw new RuntimeException("Error when reading clanlevels.yml", e);
            }
        }

        ClanLevelCache.initializeLevel(level, this);
    }

}
