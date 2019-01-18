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

    public static void register(Zone zone, Info info) {
        String cacheKey = "zone:" + info.getServerInfo().getName();
        Map<String, Object> map = L2Pool.map();
        if (!map.containsKey(cacheKey)) {
            map.put(cacheKey, zone);
        }
        zone.put(info);
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
        for (Zone zone : mapping.values()) {
            for (Info info : zone.getAll().values()) {
                who.sendMessage("- id: " + info.getServerInfo().getName() + info.getServerInfo().getAddress());
                int value = info.getValue();
                who.sendMessage("  priority: " + value);
            }
            who.sendMessage("# zone " + zone.getPattern().pattern() + " " + zone.alive(-1, null).size() + " alive servers");
        }
    }

    public void sendHead(CommandSender who) {
        for (Zone zone : mapping.values()) {
            Info info = InfoMgr.getByName(zone.alive(1, null).peek());
            who.sendMessage("- id: " + info.getServerInfo().getName() + info.getServerInfo().getAddress());
            int value = info.getValue();
            who.sendMessage("  priority: " + value);
        }
    }

}
