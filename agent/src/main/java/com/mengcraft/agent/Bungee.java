package com.mengcraft.agent;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * Created on 16-9-8.
 */
public class Bungee extends Plugin implements Listener {

    @Override
    public void onEnable() {
        getProxy().registerChannel(Message.CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(PluginMessageEvent event) {
        if (event.getTag().equals(Message.CHANNEL) && event.getSender() instanceof Server) {
            Message message = Message.decode(event.getData());
            if (message.getExecutor() == Executor.BUNGEE) {
                for (String command : message.getCommand()) {
                    getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command);
                }
            } else {
                String sender = Server.class.cast(event.getSender()).getInfo().getName();
                getProxy().getServers().forEach((name, info) -> {
                    if (!sender.equals(name)) {
                        info.sendData(Message.CHANNEL, event.getData(), message.isQueued());
                    }
                });
            }
            event.setCancelled(true);
        }
    }

}
