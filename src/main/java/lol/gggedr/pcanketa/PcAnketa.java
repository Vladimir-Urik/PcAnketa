package lol.gggedr.pcanketa;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lol.gggedr.pcanketa.managers.Managers;
import lol.gggedr.pcanketa.managers.impl.CommandsManager;
import lol.gggedr.pcanketa.managers.impl.DatabaseManager;
import lol.gggedr.pcanketa.managers.impl.FilesManager;
import lol.gggedr.pcanketa.managers.impl.PlayersManager;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "pcanketa",
        name = "PcAnketa",
        version = BuildConstants.VERSION
)
public class PcAnketa {

    private static PcAnketa instance;
    private final Logger logger;
    private final ProxyServer server;
    private final Path dataDirectory;

    @Inject
    public PcAnketa(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
        this.logger = logger;
        this.server = server;
        this.dataDirectory = dataDirectory;

        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Managers.registerManager(new FilesManager());
        Managers.registerManager(new CommandsManager());
        Managers.registerManager(new DatabaseManager());
        Managers.registerManager(new PlayersManager());

        Managers.enableManagers();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        Managers.disableManagers();
    }

    public static PcAnketa getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }
}
