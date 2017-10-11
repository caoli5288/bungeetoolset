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

    public Info get(ServerInfo serverInfo) {
        return mapping.compute(serverInfo.getName(), (key, oldValue) -> {
            if (oldValue == null || !oldValue.getServerInfo().equals(serverInfo)) {
                return Info.of(serverInfo);
            }
            return oldValue;
        });
    }
}
