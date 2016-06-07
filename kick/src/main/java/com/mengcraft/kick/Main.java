package com.mengcraft.kick;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

/**
 * Created on 16-5-30.
 */
public class Main extends Plugin implements Listener {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(PreLoginEvent event) {
        ProxiedPlayer connected = getProxy().getPlayer(event.getConnection().getName());
        if (connected != null && eq(connected.getAddress(), event.getConnection().getAddress())) {
            connected.disconnect("你在别处登录了");
        }
    }

    private boolean eq(InetSocketAddress i, InetSocketAddress j) {
        return eq(i.getAddress().getAddress(), j.getAddress().getAddress());
    }

    private boolean eq(byte[] i, byte[] j) {
        for (int k = 0; k < i.length; k++) {
            if (i[k] != j[k]) {
                return false;
            }
        }
        return true;
    }

}
