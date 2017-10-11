package com.mengcraft.lobbybalancer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by on 10-11.
 */
public enum ZoneMgr {

    INST;

    private final Cache<String, Zone> mapping = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .removalListener(notice -> invalid((Zone) notice.getValue()))
            .build();

    private final Cache<String, Zone> sl = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final List<Pattern> all = new ArrayList<>();

    public void invalid(Zone zone) {
        for (Info i : zone.getQueue()) {
            sl.invalidate(i.getServerInfo().getName());
        }
    }

    @SneakyThrows
    public Zone select(ServerInfo info) {
        return sl.get(info.getName(), () -> {
            for (Pattern p : all) {
                if (p.matcher(info.getName()).matches()) {
                    return mapping.get(p.pattern(), () -> Zone.build(p));
                }
            }
            return Zone.NIL;
        });
    }

    public void updateAll() {
        updateAll(null);
    }

    @SneakyThrows
    public void updateAll(CommandSender who) {
        mapping.invalidateAll();
        for (Pattern p : all) {
            mapping.get(p.pattern(), () -> Zone.build(p));
        }
        if (!(who == null)) who.sendMessage("Okay");
    }

    public void add(Pattern pattern) {
        all.add(pattern);
    }

    public void sendAll(CommandSender who) {
        who.sendMessage(String.valueOf(mapping.asMap().values()));
    }

    public void sendHead(CommandSender who) {
        for (Zone zone : mapping.asMap().values()) {
            who.sendMessage("Zone(pattern=\"" + zone.getPattern() + "\", head=\"" + zone.alive().poll() + "\")");
        }
    }

}
