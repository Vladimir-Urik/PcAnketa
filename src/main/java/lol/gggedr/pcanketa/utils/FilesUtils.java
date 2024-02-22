package lol.gggedr.pcanketa.utils;

import lol.gggedr.pcanketa.PcAnketa;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class FilesUtils {

    public static CommentedConfigurationNode loadConfig(String name) {
        var path = PcAnketa.getInstance().getDataDirectory().resolve(name);

        if(!path.toFile().exists()) {
            try {
                // Copy from jar
                try (var is = Objects.requireNonNull(PcAnketa.class.getResourceAsStream("/"+ name))) {
                    if (is != null) {
                        Files.copy(is, path);
                    } else {
                        throw new Exception("Resource not found");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        var loader = YamlConfigurationLoader.builder()
                .path(path)
                .build();

        try {
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
