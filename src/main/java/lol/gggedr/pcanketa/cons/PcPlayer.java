package lol.gggedr.pcanketa.cons;

import lol.gggedr.pcanketa.managers.Managers;
import lol.gggedr.pcanketa.managers.impl.DatabaseManager;

import java.util.concurrent.CompletableFuture;

public class PcPlayer {

    private final String name;
    private boolean voted = false;
    private boolean loaded = false;

    public PcPlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean hasVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void vote(String value) {
        voted = true;
        CompletableFuture.runAsync(() -> {
            try {
                Managers.getManager(DatabaseManager.class).useConnection((c) -> {
                    try(var statement = c.prepareStatement("INSERT INTO votes (nick, value) VALUES (?,?)")) {
                        statement.setString(1, name);
                        statement.setString(2, value);
                        statement.executeUpdate();

                        // TODO: Odmena
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
