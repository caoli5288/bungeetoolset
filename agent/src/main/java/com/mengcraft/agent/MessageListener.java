package com.mengcraft.agent;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;

/**
 * Created on 16-9-28.
 */
public class MessageListener implements PluginMessageListener {

    private final Main main;

    public MessageListener(Main main) {
        this.main = main;
    }

    @Override
    public void onPluginMessageReceived(String label, Player p, byte[] data) {
        Message message = Message.decode(data);
        if (message.getExecutor() == Executor.BUKKIT) {
            execute(message.getCommand());
        }
    }

    private void execute(List<String> command) {
        CommandSender sender = main.getServer().getConsoleSender();
        command.forEach(line -> {
            main.getServer().dispatchCommand(sender, line);
        });
    }

}
