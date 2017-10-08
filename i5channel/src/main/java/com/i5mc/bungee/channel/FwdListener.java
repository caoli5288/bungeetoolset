package com.i5mc.bungee.channel;

import lombok.val;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created on 17-3-15.
 */
public class FwdListener extends Plugin implements Listener {

    interface Expr {

        boolean match(String key);
    }

    public static final Map<String, Expr> MAP = new HashMap<>();
    public static final Expr NIL = i -> true;

    @Override
    public void onEnable() {
        getProxy().registerChannel(ChannelMessage.CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(PluginMessageEvent event) {
        val recv = event.getReceiver();// NPE here. possible bungeecord's bug
        if (recv == null || recv.getClass() == ServerConnection.class) return;
        if (event.getTag().equals(ChannelMessage.CHANNEL)) {
            event.setCancelled(true);
            try {
                val input = ChannelMessage.decode(event.getData());
                input.sender = ((Server) event.getSender()).getInfo().getName();
                input.sent = $.now();
                handle(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(ChannelMessage message) {
        val receiver = message.getReceiver();
        val match = $.nil(receiver) ? NIL : MAP.computeIfAbsent(receiver, key -> {
            val expr = Pattern.compile(key);
            return any -> expr.matcher(any).matches();
        });
        val out = message.encode();
        getProxy().getServers().forEach((key, info) -> {
            if (match.match(key) && !message.sender.equals(key)) {
                info.sendData(ChannelMessage.CHANNEL, out, message.isQueued());
            }
        });
    }

}
