package com.mengcraft.bunllect;

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
        EntityQueue.QUEUE.offer(new Entity(
                event.getPlayer().getName(),
                event.getPlayer().getAddress().getAddress().getHostAddress(),
                getLife(event.getPlayer().getName()),
                event.getPlayer().getServer().getInfo().getName(),
                host
        ));
    }

    private int getLife(String name) {
        Long remove = time.remove(name);
        if (remove == null) {
            return 0;
        }
        return Math.toIntExact((System.currentTimeMillis() - remove) / 1000);
    }

}
