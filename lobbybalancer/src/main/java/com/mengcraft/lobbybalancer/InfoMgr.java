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

    public Info mapping(ServerInfo serverInfo) {
        return mapping.compute(serverInfo.getName(), (key, oldValue) -> {
            if (oldValue == null || !oldValue.getServerInfo().equals(serverInfo)) {
                return Info.build(serverInfo);
            }
            return oldValue;
        });
    }

    public Info getByName(String name) {
        return mapping.get(name);
    }

    public boolean check(ServerInfo serverInfo) {
        return mapping.containsKey(serverInfo.getName());
    }
}
