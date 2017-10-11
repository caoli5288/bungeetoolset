package com.mengcraft.lobbybalancer;

import lombok.val;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
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
    private final Map<UUID, String> join = new ConcurrentHashMap<>();

    @EventHandler
    public void handle(ServerConnectEvent event) {
        val id = event.getPlayer().getUniqueId();
        if (locking.containsKey(id)) {
            return;
        }

        val old = event.getTarget();
        val zone = ZoneMgr.INST.select(old);
        if (zone == Zone.NIL) {
            return;
        }

        locking.put(id, "");
        val player = ((UserConnection) event.getPlayer());
        if (player.getServer() == null) {
            player.setServerJoinQueue(zone.alive());
            val head = zone.peek();
            if (!(head == null)) {
                event.setTarget(head.getServerInfo());
            }
            join.put(id, "");
        } else {
            zone.connect(player, result -> {
                if (result) {
                    locking.remove(id);
                } else {
                    player.connect(old, (i, err) -> locking.remove(id));
                }
            });
            event.setCancelled(true);
        }

        Main.log("Redirect player " + zone.getPattern().pattern());
    }

    public void unlock(UUID id, Server server) {
        locking.remove(id);
        val removal = join.remove(id);
        if (removal == null || server == null) {
            return;
        }

        val info = server.getInfo();
        if (!InfoMgr.INST.exist(info)) {
            return;
        }

        val i = InfoMgr.INST.get(info);
        if (i.incValue() >= -1) {
            i.update(() -> ZoneMgr.INST.select(info).queue(i));
        }
    }

    @EventHandler
    public void handle(ServerConnectedEvent event) {
        unlock(event.getPlayer().getUniqueId(), event.getServer());
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        unlock(event.getPlayer().getUniqueId(), null);
    }

}
