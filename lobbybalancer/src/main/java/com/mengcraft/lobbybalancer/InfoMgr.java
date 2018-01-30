package com.mengcraft.lobbybalancer;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by on 10-11.
 */
public enum InfoMgr {

    INSTANCE;

    private final Map<String, Info> mapping = new ConcurrentHashMap<>();

    public static Info select(ServerInfo serverInfo) {
        Info info = INSTANCE.mapping.get(serverInfo.getName());
        if (info == null) {
            INSTANCE.mapping.put(serverInfo.getName(), info = new Info(serverInfo));
        } else if (!info.getServerInfo().equals(serverInfo)) {
            ScheduledTask later = info.getLater();
            if (!$.nil(later)) {
                later.cancel();
            }
            INSTANCE.mapping.put(serverInfo.getName(), info = new Info(serverInfo));
        }
        return info;
    }

    public static Info getByName(String name) {
        return INSTANCE.mapping.get(name);
    }

    public static boolean mapped(ServerInfo serverInfo) {
        return INSTANCE.mapping.containsKey(serverInfo.getName());
    }
}
