package com.mengcraft.bunllect;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 16-5-30.
 */
public class Executor implements Listener {

    private final Map<String, Long> time = new HashMap<>();
    private final String host;

    public Executor(String host) {
        this.host = host;
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        time.put(event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        int life = getLife(p.getName());
        if (p.getServer() != null) {
            EntityQueue.QUEUE.offer(new Entity(
                    p.getName(),
                    p.getAddress().getAddress().getHostAddress(),
                    life,
                    p.getServer().getInfo().getName(),
                    host
            ));
        }
    }

    private int getLife(String name) {
        Long remove = time.remove(name);
        if (remove == null) {
            return 0;
        }
        return Math.toIntExact((System.currentTimeMillis() - remove) / 1000);
    }

}
