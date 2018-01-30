package com.mengcraft.lobbybalancer;

import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by on 10-11.
 */
public enum ZoneMgr {

    INSTANCE;

    private final Map<String, Zone> mapping = new ConcurrentHashMap<>();

    @SneakyThrows
    public static Zone select(ServerInfo info) {
        return select(info.getName());
    }

    @SneakyThrows
    public static Zone select(String name) {
        return L2Pool.load("zone:" + name, () -> {
            for (Zone zone : INSTANCE.mapping.values()) {
                Pattern pattern = zone.getPattern();
                if (pattern.matcher(name).matches()) return zone;
            }
            return null;
        });
    }

    @SneakyThrows
    public void updateAll(CommandSender who) {
        for (Zone zone : INSTANCE.mapping.values()) {
            zone.update();
        }
        if (!(who == null)) who.sendMessage("Okay");
    }

    public void add(Pattern pattern) {
        INSTANCE.mapping.put(pattern.pattern(), Zone.build(pattern));
    }

    public void sendAll(CommandSender who) {
        mapping.forEach((__, zone) -> {
            zone.getAll().forEach((id, info) -> {
                who.sendMessage("- id: " + id + info.getServerInfo().getAddress());
                int value = info.getValue();
                if (!(value == 0)) {
                    who.sendMessage("  priority: " + value);
                }
            });
        });
    }

    public void sendHead(CommandSender who) {
        for (Zone zone : mapping.values()) {
            who.sendMessage("Zone(pattern=\"" + zone.getPattern() + "\", head=\"" + zone.alive().peek() + "\")");
        }
    }

}
