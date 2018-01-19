package com.i5mc.bungee.list.rt;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 17-7-19.
 */
@Data
@EqualsAndHashCode(of = "name")
public class InfoGroup {

    private final Map<String, Info> handle;
    private final String name;

    InfoGroup(String name) {
        this.name = name;
        handle = new HashMap<>();
    }

    public void alive(String host, int port, boolean fixedId) {
        val info = handle.computeIfAbsent(host.replace('.', '_') + "_" + port, key -> b(key, new InetSocketAddress(host, port), fixedId));
        if (info.getAlive() == -1) {
            val all = BungeeCord.getInstance().getServers();
            synchronized (all) {
                all.put(info.getHandle().getName(), info.getHandle());
            }
            BungeeCord.getInstance().getPluginManager().callEvent(new RuntimeServerEvent(info.getHandle(), RuntimeServerEvent.Action.UP));
            RTServer.log(info.getHandle().getName() + info.getHandle().getAddress() + " online", true);
        }
        info.setAlive(1);
    }

    public void valid() {
        handle.forEach((key, info) -> {
            if (!(info.getAlive() == -1) && !info.valid()) {
                val all = BungeeCord.getInstance().getServers();
                synchronized (all) {
                    all.remove(info.getHandle().getName());
                }
                BungeeCord.getInstance().getPluginManager().callEvent(new RuntimeServerEvent(info.getHandle(), RuntimeServerEvent.Action.DOWN));
                RTServer.log(info.getHandle().getName() + info.getHandle().getAddress() + " offline", true);
            }
        });
    }

    private Info b(String key, InetSocketAddress remote, boolean fixedId) {
        val i = new BungeeServerInfo(name + "-" + ((RT.INSTANCE.isFixedId() || fixedId) ? key : (handle.size() + 1)), remote, "", false);
        BungeeCord.getInstance().getServers().put(i.getName(), i);
        return new Info(i, -1);
    }

}
