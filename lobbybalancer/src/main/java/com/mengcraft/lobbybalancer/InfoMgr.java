package com.mengcraft.lobbybalancer;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by on 10-11.
 */
public enum InfoMgr {

    INST;

    private final Map<String, Info> mapping = new ConcurrentHashMap<>();

    public static Info mapping(ServerInfo serverInfo) {
        return INST.mapping.compute(serverInfo.getName(), (key, old) -> {
            if (old == null || !old.getServerInfo().equals(serverInfo)) {
                return Info.bind(serverInfo);
            }
            return old;
        });
    }

    public static Info getByName(String name) {
        return INST.mapping.get(name);
    }

    public static boolean check(ServerInfo serverInfo) {
        return INST.mapping.containsKey(serverInfo.getName());
    }
}
