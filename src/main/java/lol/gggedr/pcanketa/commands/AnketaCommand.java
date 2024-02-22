package lol.gggedr.pcanketa.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lol.gggedr.pcanketa.cons.Result;
import lol.gggedr.pcanketa.managers.Managers;
import lol.gggedr.pcanketa.managers.impl.DatabaseManager;
import lol.gggedr.pcanketa.managers.impl.FilesManager;
import lol.gggedr.pcanketa.managers.impl.PlayersManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Objects;

public class AnketaCommand {

    public static BrigadierCommand createBrigadierCommand() {
        LiteralArgumentBuilder<CommandSource> anketaNode = BrigadierCommand.literalArgumentBuilder("anketa")
                .requires(source -> source.hasPermission("pcanketa.anketa"))
                .executes(context -> {
                    var player = (Player) context.getSource();
                    var playerName = player.getUsername();
                    var playersManager = Managers.getManager(PlayersManager.class);
                    var messagesNode = Managers.getManager(FilesManager.class).getMessagesConfig();

                    var pcPlayer = playersManager.getPlayer(playerName);
                    if(!pcPlayer.isLoaded()) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(messagesNode.node("not-loaded").getString(), "not-loaded message is not set in messages.yml")));
                        return Command.SINGLE_SUCCESS;
                    }

                    if(pcPlayer.hasVoted()) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(messagesNode.node("already-voted").getString(), "already-voted message is not set in messages.yml")));
                        return Command.SINGLE_SUCCESS;
                    }

                    StringBuilder message = new StringBuilder();
                    try {
                        Objects.requireNonNull(Managers.getManager(FilesManager.class).getMessagesConfig()
                                        .node("anketa")
                                        .getList(String.class))
                                        .forEach(s -> message.append(s).append("<newline>"));
                    } catch (SerializationException e) {
                        throw new RuntimeException(e);
                    }

                    var component = MiniMessage.miniMessage().deserialize(message.toString());
                    context.getSource().sendMessage(component);
                    return Command.SINGLE_SUCCESS;
                }).then(BrigadierCommand.literalArgumentBuilder("reload")
                        .requires(source -> source.hasPermission("pcanketa.anketa.reload"))
                        .executes(context -> {
                            Managers.getManager(FilesManager.class).reloadMessagesConfig();
                            context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Messages reloaded!"));
                            return Command.SINGLE_SUCCESS;
                        }).build()
                );

        var optionsNode = Managers.getManager(FilesManager.class).getConfig().node("options");
        var options = optionsNode.childrenMap().keySet();
        for (var option : options) {
            var stringOption = Integer.toString(((int) option));
            var optionNode = optionsNode.node(option);
            anketaNode.then(BrigadierCommand.literalArgumentBuilder(stringOption)
                    .requires(source -> source.hasPermission("pcanketa.anketa"))
                    .executes(context -> {
                        var player = (Player) context.getSource();
                        var playerName = player.getUsername();
                        var playersManager = Managers.getManager(PlayersManager.class);
                        var messagesNode = Managers.getManager(FilesManager.class).getMessagesConfig();

                        var pcPlayer = playersManager.getPlayer(playerName);
                        if(!pcPlayer.isLoaded()) {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(messagesNode.node("not-loaded").getString(), "not-loaded message is not set in messages.yml")));
                            return Command.SINGLE_SUCCESS;
                        }

                        var optionValue = optionNode.getString();
                        if(pcPlayer.hasVoted()) {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(messagesNode.node("already-voted").getString(), "already-voted message is not set in messages.yml")));
                            return Command.SINGLE_SUCCESS;
                        }

                        pcPlayer.vote(optionValue);
                        player.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(messagesNode.node("voted").getString(), "voted message is not set in messages.yml")));

                        return Command.SINGLE_SUCCESS;
                    }).build()
            );
        }

        anketaNode.then(BrigadierCommand.literalArgumentBuilder("stats")
                .requires(source -> source.hasPermission("pcanketa.anketa.stats"))
                .executes(context -> {
                    var data = Managers.getManager(DatabaseManager.class).getCount();

                    var miniMessage = MiniMessage.miniMessage();
                    StringBuilder message = new StringBuilder("""
                            
                            <bold><green>VÝSLEDKY ANKETY</green></bold>

                            """);

                    for (var result : data) {
                        message.append("   <color:#ffb49c>")
                                .append(result.value())
                                .append("</color> <dark_gray>|</dark_gray> ")
                                .append(result.percentage())
                                .append("<color:#f6bfff>%</color> <dark_gray>(</dark_gray>")
                                .append(result.count())
                                .append("<color:#96ffc7>x</color><dark_gray>)</dark_gray>")
                                .append("\n");
                    }

                    var count = data.stream().mapToLong(Result::count).sum();

                    message.append("\n\nSpolu hlasovalo: <green>")
                            .append(count)
                            .append("</green> hráčů.")
                            .append("\n");

                    var finalMessage = miniMessage.deserialize(message.toString().replace("\n", "<newline>"));
                    context.getSource().sendMessage(finalMessage);

                    return Command.SINGLE_SUCCESS;
                }).build()
        );

        return new BrigadierCommand(anketaNode.build());
    }

}
