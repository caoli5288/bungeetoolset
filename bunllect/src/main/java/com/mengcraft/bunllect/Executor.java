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
        String who = p.getName();
        int life = getLife(who);
        if (life > 0 && p.getServer() != null) {
            String ip = p.getAddress().getAddress().getHostAddress();
            EntityQueue.QUEUE.offer(new Entity(
                    who,
                    ip,
                    life,
                    p.getServer().getInfo().getName(),
                    host
            ));
            EntityQueue.QUEUE.offer(new EntityTotal(
                    who,
                    p.getUniqueId(),
                    ip,
                    life
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
