package lol.gggedr.pcanketa.managers.impl;

import com.velocitypowered.api.command.CommandManager;
import lol.gggedr.pcanketa.commands.AnketaCommand;
import lol.gggedr.pcanketa.managers.Manager;

public class CommandsManager implements Manager {

    @Override
    public void onEnable() {
        var server = getInstance().getServer();
        var commandManager = server.getCommandManager();

        var anketaCommand = AnketaCommand.createBrigadierCommand();
        var anketaCommandMeta = commandManager.metaBuilder("anketa")
                .plugin(this)
                .build();

        commandManager.register(anketaCommandMeta, anketaCommand);
    }

    @Override
    public void onDisable() {

    }

}
