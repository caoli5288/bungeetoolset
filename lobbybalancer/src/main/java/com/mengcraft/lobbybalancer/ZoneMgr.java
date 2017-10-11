package com.mengcraft.lobbybalancer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import lombok.val;
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
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final List<Pattern> all = new ArrayList<>();

    @SneakyThrows
    public Zone select(ServerInfo info) {
        val itr = all.iterator();
        while (itr.hasNext()) {
            Pattern p = itr.next();
            if (p.matcher(info.getName()).matches()) {
                return mapping.get(p.pattern(), () -> Zone.build(p));
            }
        }
        return null;
    }

    @SneakyThrows
    public void updateAll() {
        mapping.invalidateAll();
        for (Pattern p : all) {
            Zone zone = mapping.get(p.pattern(), () -> Zone.build(p));
            Main.log("Fetch " + zone.size() + " server(s) for pattern " + p.pattern());
        }
    }

    public void sendAll(CommandSender who) {
        who.sendMessage(String.valueOf(mapping));
    }

    public void add(Pattern pattern) {
        all.add(pattern);
    }

}
