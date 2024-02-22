package lol.gggedr.pcanketa.managers;

import lol.gggedr.pcanketa.PcAnketa;

public interface Manager {

    public void onEnable();

    public void onDisable();

    default PcAnketa getInstance() {
        return PcAnketa.getInstance();
    }

    default <T extends Manager> T getManager(Class<T> clazz) {
        return Managers.getManager(clazz);
    }
}
