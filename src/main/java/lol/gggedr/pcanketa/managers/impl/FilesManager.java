package lol.gggedr.pcanketa.managers.impl;

import lol.gggedr.pcanketa.managers.Manager;
import lol.gggedr.pcanketa.utils.FilesUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public class FilesManager implements Manager {

    private CommentedConfigurationNode messagesConfig;
    private CommentedConfigurationNode config;

    @Override
    public void onEnable() {
        getInstance().getDataDirectory().toFile().mkdirs();

        messagesConfig = FilesUtils.loadConfig("messages.yml");
        config = FilesUtils.loadConfig("config.yml");
    }

    @Override
    public void onDisable() {

    }

    public CommentedConfigurationNode getMessagesConfig() {
        return messagesConfig;
    }

    public CommentedConfigurationNode getConfig() {
        return config;
    }

    public void reloadMessagesConfig() {
        messagesConfig = FilesUtils.loadConfig("messages.yml");
    }
}
