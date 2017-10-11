package com.mengcraft.lobbybalancer;

import lombok.val;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by on 10-11.
 */
public class MainListener implements Listener {

    private final Map<UUID, String> locking = new ConcurrentHashMap<>();

    @EventHandler
    public void handle(ServerConnectEvent event) {
        val id = event.getPlayer().getUniqueId();
        if (!locking.containsKey(id)) {
            val old = event.getTarget();
            val zone = ZoneMgr.INST.select(old);
            if (!(zone == null)) {
                locking.put(id, "");
                event.setCancelled(true);
                zone.connect(event.getPlayer(), result -> {
                    if (result) {
                        locking.remove(id);
                    } else {
                        event.getPlayer().connect(old, (i, err) -> locking.remove(id));
                    }
                });
                Main.log("Redirect player from " + old.getName());
            }
        }
    }

}
