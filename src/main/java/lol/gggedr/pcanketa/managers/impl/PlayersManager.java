package lol.gggedr.pcanketa.managers.impl;

import lol.gggedr.pcanketa.PcAnketa;
import lol.gggedr.pcanketa.cons.PcPlayer;
import lol.gggedr.pcanketa.listeners.PlayersListener;
import lol.gggedr.pcanketa.managers.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayersManager implements Manager {

    private final List<PcPlayer> players = new ArrayList<>();

    @Override
    public void onEnable() {
        var eventManager = PcAnketa.getInstance().getServer().getEventManager();
        eventManager.register(PcAnketa.getInstance(), new PlayersListener());
    }

    @Override
    public void onDisable() {
        players.clear();
    }

    public List<PcPlayer> getPlayers() {
        return players;
    }

    public PcPlayer getPlayer(String name) {
        return players.stream().filter(player -> player.getName().equals(name)).findFirst().orElse(new PcPlayer(name));
    }

    public void loadPlayer(String name) {
        CompletableFuture.runAsync(() -> {
            try {
                getManager(DatabaseManager.class).useConnection((c) -> {
                    try(var statement = c.prepareStatement("SELECT * FROM votes WHERE nick=?")) {
                        statement.setString(1, name);
                        try(var result = statement.executeQuery()) {
                            var player = new PcPlayer(name);
                            player.setLoaded(true);
                            player.setVoted(result.next());

                            unloadPlayer(name);
                            players.add(player);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void unloadPlayer(String name) {
        players.removeIf(player -> player.getName().equalsIgnoreCase(name));
    }
}
