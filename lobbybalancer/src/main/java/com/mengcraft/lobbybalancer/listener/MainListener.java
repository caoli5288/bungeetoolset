package com.mengcraft.lobbybalancer.listener;

import com.mengcraft.lobbybalancer.$;
import com.mengcraft.lobbybalancer.Info;
import com.mengcraft.lobbybalancer.InfoMgr;
import com.mengcraft.lobbybalancer.Zone;
import com.mengcraft.lobbybalancer.ZoneMgr;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.mengcraft.lobbybalancer.$.nil;

/**
 * Created by on 10-11.
 */
public class MainListener implements Listener {

    private final Map<UUID, String> join = new ConcurrentHashMap<>();

    @EventHandler
    public void handle(ServerConnectEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (join.containsKey(id)) {
            return;
        }

        Zone zone = ZoneMgr.select(event.getTarget());
        if (nil(zone)) {
            return;
        }

        UserConnection p = ((UserConnection) event.getPlayer());
        Queue<String> alive = zone.alive();
        String target = alive.peek();

        if (!nil(target)) {
            join.put(id, target);

            ServerInfo targetInfo = InfoMgr.getByName(target).getServerInfo();
            p.setServerJoinQueue(alive);

            if (p.getServer() == null) {
                event.setTarget(targetInfo);
            } else {
                event.setCancelled(true);
                p.connect(targetInfo, null, true);
            }

            $.log("Redirect " + p.getName() + " to " + target);
        }
    }

    @EventHandler
    public void handle(ServerConnectedEvent event) {
        unlock(event.getPlayer().getUniqueId(), event.getServer());
    }

    public void unlock(UUID id, Server server) {
        String target = join.remove(id);

        if (target == null || server == null) {
            return;
        }

        ServerInfo serverInfo = server.getInfo();
        if (!InfoMgr.mapped(serverInfo)) {
            return;
        }

        Zone zone = ZoneMgr.select(serverInfo);

        Info info = InfoMgr.select(serverInfo);

        info.incValue();
        info.update(zone);

        if (!serverInfo.getName().equals(target)) {
            info = InfoMgr.getByName(target);
            info.update(zone);
        }
    }

    @EventHandler
    public void handle(ServerDisconnectEvent event) {
        Info info = InfoMgr.getByName(event.getTarget().getName());
        if (!nil(info)) {
            info.decValue();
        }
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        unlock(event.getPlayer().getUniqueId(), null);
    }

    public void joinForce(UserConnection p, String id) {
        ServerInfo info = BungeeCord.getInstance().getServerInfo(id);
        if (info == null) throw new NullPointerException("info");

        join.put(p.getUniqueId(), id);
        p.connect(info);
    }

}
