package com.mengcraft.lobbybalancer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by on 10-11.
 */
public enum ZoneMgr {

    INST;

    private final Map<String, Zone> mapping = new ConcurrentHashMap<>();

    private final Cache<String, Zone> query = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    private final List<Pattern> all = new ArrayList<>();

    @SneakyThrows
    public static Zone select(ServerInfo info) {
        return INST.query.get(info.getName(), () -> {
            for (Pattern p : INST.all) {
                if (p.matcher(info.getName()).matches()) {
                    Zone zone = INST.mapping.computeIfAbsent(p.pattern(), pt -> Zone.build(p));
                    if (zone.outdated()) {
                        zone.update();
                    }
                    return zone;
                }
            }
            return Zone.NIL;
        });
    }

    @SneakyThrows
    public void updateAll(CommandSender who) {
        for (Pattern p : all) {
            Zone zone = mapping.get(p.pattern());
            if ($.nil(zone)) {
                mapping.put(p.pattern(), Zone.build(p));
            } else {
                zone.update();
            }
        }
        if (!(who == null)) who.sendMessage("Okay");
    }

    public void add(Pattern pattern) {
        all.add(pattern);
    }

    public void sendAll(CommandSender who) {
        who.sendMessage(String.valueOf(mapping));
    }

    public void sendHead(CommandSender who) {
        for (Zone zone : mapping.values()) {
            who.sendMessage("Zone(pattern=\"" + zone.getPattern() + "\", head=\"" + zone.alive().poll() + "\")");
        }
    }

}
