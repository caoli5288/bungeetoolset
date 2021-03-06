package com.mengcraft.bunllect;

import com.mengcraft.bunllect.entity.Entity;
import com.mengcraft.bunllect.entity.EntityTotal;
import io.netty.channel.Channel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.ChannelWrapper;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 16-5-30.
 */
public class Executor implements Listener {

    private final Map<String, Timestamp> time = new HashMap<>();
    private final String host;

    public Executor(String host) {
        this.host = host;
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer who = event.getPlayer();
        EntityQueue.QUEUE.offer(new EntityTotal(who.getName(),
                who.getUniqueId(),
                null,
                -1,
                null,
                true
        ));
        time.put(who.getName(), $.now());
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        String who = p.getName();
        int life = getLife(who);
        if (life > 0 && p.getServer() != null) {
            String ip = p.getAddress().getAddress().getHostAddress();
            InitialHandler h = (InitialHandler) p.getPendingConnection();
            ChannelWrapper wrapper = RefHelper.getField(h, InitialHandler.class == h.getClass() ? "ch" : "channel");
            Channel ch = RefHelper.getField(wrapper, "ch");
            EntityQueue.QUEUE.offer(new Entity(
                    who,
                    ip,
                    life,
                    p.getServer().getInfo().getName(),
                    host,
                    ch.localAddress().toString().substring(1)
            ));
            EntityQueue.QUEUE.offer(new EntityTotal(
                    who,
                    p.getUniqueId(),
                    ip,
                    life,
                    null,
                    false
            ));
        }
    }

    private int getLife(String name) {
        Timestamp remove = time.remove(name);
        if (remove == null) {
            return 0;
        }
        return Math.toIntExact((System.currentTimeMillis() - remove.getTime()) / 1000);
    }

}
