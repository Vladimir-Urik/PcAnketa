package lol.gggedr.pcanketa.managers;

import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Managers {

    private static final List<Manager> managers = new ArrayList<>();

    public static void registerManager(Manager manager) {
        managers.add(manager);
    }

    public static void enableManagers() {
        managers.forEach(Manager::onEnable);
    }

    public static void disableManagers() {
        managers.forEach(Manager::onDisable);
    }

    public static <T extends Manager> T getManager(Class<T> clazz) {
        return (T) managers.stream().filter(clazz::isInstance).findFirst().orElse(null);
    }

}
