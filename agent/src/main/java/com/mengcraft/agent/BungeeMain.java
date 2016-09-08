package com.mengcraft.agent;

import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * Created on 16-9-8.
 */
public class BungeeMain extends Plugin implements Listener {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(PluginMessageEvent event) {
        if (event.getTag().equals(Message.CHANNEL) && event.getSender() instanceof Server) {
            Message message = Message.decode(event.getData());
            for (String command : message.getCommandList()) {
                getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command);
            }
        }
    }

}
