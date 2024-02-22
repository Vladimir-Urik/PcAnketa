package lol.gggedr.pcanketa.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import lol.gggedr.pcanketa.managers.Managers;
import lol.gggedr.pcanketa.managers.impl.PlayersManager;

public class PlayersListener {

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        var player = event.getPlayer();
        var name = player.getUsername();

        var manager = Managers.getManager(PlayersManager.class);
        manager.loadPlayer(name);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        var player = event.getPlayer();
        var name = player.getUsername();

        var manager = Managers.getManager(PlayersManager.class);
        manager.unloadPlayer(name);
    }

}
