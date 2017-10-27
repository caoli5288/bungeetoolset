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
        val alive = zone.alive();
        String target = alive.peek();

        if (!(target == null)) {
            player.setServerJoinQueue(alive);
            join.put(id, target);
            val targetInfo = InfoMgr.getByName(target).getServerInfo();
            if (player.getServer() == null) {
                event.setTarget(targetInfo);
            } else {
                event.setCancelled(true);
                player.connect(targetInfo, null, true);
            }
        }

        Main.log("Redirect " + player.getName() + " by " + zone.getPattern().pattern());
    }

    public void unlock(UUID id, Server server) {
        String target = join.remove(id);
        locking.remove(id);

        if (target == null || server == null) {
            return;
        }

        ServerInfo serverInfo = server.getInfo();
        if (!InfoMgr.check(serverInfo)) {
            return;
        }

        Zone zone = ZoneMgr.INST.select(serverInfo);
        Info i = InfoMgr.mapping(serverInfo);

        if (i.outdated() || i.incValue() > -1) {
            i.update(() -> zone.queue(i));
        } else {
            zone.queue(i);
        }

        if (!serverInfo.getName().equals(target)) {
            val info = InfoMgr.getByName(target);
            info.update(() -> zone.queue(info));
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
