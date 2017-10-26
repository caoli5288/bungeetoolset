package com.mengcraft.lobbybalancer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import lombok.val;
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

    private final Map<String, Pair<Zone, Long>> mapping = new ConcurrentHashMap<>();

    private final Cache<String, Zone> query = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    private final List<Pattern> all = new ArrayList<>();

    @SneakyThrows
    public Zone select(ServerInfo info) {
        return query.get(info.getName(), () -> {
            for (Pattern p : all) {
                if (p.matcher(info.getName()).matches()) {
                    Pair<Zone, Long> pair = mapping.computeIfAbsent(p.pattern(), pt -> new Pair<>(Zone.build(p), System.currentTimeMillis()));
                    if (pair.getValue() > System.currentTimeMillis() - 60000) {
                        return mapping.compute(p.pattern(), (pt, i) -> new Pair<>(Zone.build(p), System.currentTimeMillis())).getKey();
                    }
                    return pair.getKey();
                }
            }
            return Zone.NIL;
        });
    }

    @SneakyThrows
    public void updateAll(CommandSender who) {
        mapping.clear();
        for (Pattern p : all) {
            mapping.put(p.pattern(), new Pair<>(Zone.build(p), System.currentTimeMillis()));
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
        for (Pair<Zone, Long> p : mapping.values()) {
            val zone = p.getKey();
            who.sendMessage("Zone(pattern=\"" + zone.getPattern() + "\", head=\"" + zone.alive().poll() + "\")");
        }
    }

}
