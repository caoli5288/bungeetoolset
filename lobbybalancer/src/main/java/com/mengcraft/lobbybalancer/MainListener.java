package com.mengcraft.lobbybalancer;

import lombok.val;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.Queue;
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
            Queue<String> alive = zone.alive();
            player.setServerJoinQueue(alive);
            val head = alive.peek();
            if (!(head == null)) {
                event.setTarget(InfoMgr.INST.getByName(head).getServerInfo());
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
        String removal = join.remove(id);
        locking.remove(id);

        if (removal == null || server == null) {
            return;
        }

        ServerInfo serverInfo = server.getInfo();
        if (!InfoMgr.INST.check(serverInfo)) {
            return;
        }

        Zone zone = ZoneMgr.INST.select(serverInfo);
        Info i = InfoMgr.INST.mapping(serverInfo);

        if (i.incValue() < -1) {
            zone.queue(i);
        } else {
            i.update(() -> zone.queue(i));
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
