package com.i5mc.bungee.list.rt;

import com.google.common.collect.ImmutableList;
import lombok.val;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 17-7-19.
 */
public enum RTInfoMgr {

    INSTANCE;

    private final Map<String, InfoGroup> map = new HashMap<>();

    public static void alive(String group, String host, int port) {
        val i = INSTANCE.map.computeIfAbsent(group, key -> new InfoGroup(key));
        i.alive(host, port);
    }

    public static void valid() {
        INSTANCE.map.forEach((key, group) -> group.valid());
    }

    public static List<ServerInfo> alive() {
        ImmutableList.Builder<ServerInfo> b = ImmutableList.builder();
        INSTANCE.map.forEach((key, i) -> i.getHandle().forEach((k, info) -> {
            if (!(info.getAlive() == -1)) b.add(info.getHandle());
        }));
        return b.build();
    }

    public static List<ServerInfo> alive(String group) {
        val i = INSTANCE.map.get(group);
        if (!(i == null)) {
            ImmutableList.Builder<ServerInfo> b = ImmutableList.builder();
            i.getHandle().forEach((k, info) -> {
                if (!(info.getAlive() == -1)) b.add(info.getHandle());
            });
            return b.build();
        }
        return ImmutableList.of();
    }

}
