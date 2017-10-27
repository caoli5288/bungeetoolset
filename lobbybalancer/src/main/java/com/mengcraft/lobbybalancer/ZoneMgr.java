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
        return select(info.getName());
    }

    @SneakyThrows
    public static Zone select(String name) {
        return INST.query.get(name, () -> {
            for (Pattern p : INST.all) {
                if (p.matcher(name).matches()) {
                    return INST.mapping.computeIfAbsent(p.pattern(), pt -> Zone.build(p));
                }
            }
            return Zone.NIL;
        });
    }

    @SneakyThrows
    public void updateAll(CommandSender who) {
        for (Pattern p : all) {
            mapping.compute(p.pattern(), (key, value) -> {
                if ($.nil(value)) {
                    return Zone.build(p);
                }
                return value.update();
            });
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
            who.sendMessage("Zone(pattern=\"" + zone.getPattern() + "\", head=\"" + zone.alive().peek() + "\")");
        }
    }

}
