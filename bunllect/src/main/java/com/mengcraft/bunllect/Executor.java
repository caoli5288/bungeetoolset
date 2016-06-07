package com.mengcraft.bunllect;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 16-5-30.
 */
public class Executor implements Listener {

    private final Map<String, Long> time = new HashMap<>();
    private final WriteBackend backend;

    public Executor(WriteBackend backend) {
        this.backend = backend;
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        backend.addBatch("INSERT INTO `collect_bungee` (`name`, `ip`, `type`, `type_instance`) VALUES ('" + event.getPlayer().getName() + "', '" + k(event.getPlayer().getAddress()) + "', 'join', '0')");
        time.put(event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        backend.addBatch("INSERT INTO `collect_bungee` (`name`, `ip`, `type`, `type_instance`) VALUES ('" + event.getPlayer().getName() + "', '" + k(event.getPlayer().getAddress()) + "', 'quit', '" + c(event.getPlayer().getName()) + "')");
    }

    private long c(String name) {
        Long remove = time.remove(name);
        if (remove == null) {
            return 0;
        }
        return (System.currentTimeMillis() - remove) / 1000;
    }

    private static long k(InetSocketAddress address) {
        int j = 0;
        byte[] b = address.getAddress().getAddress();
        j += (b[0] & 0xff) << 24;
        j += (b[1] & 0xff) << 16;
        j += (b[2] & 0xff) << 8;
        j += (b[3] & 0xff);
        return j & 0xffffffffL;
    }

}
