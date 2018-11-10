package com.mengcraft.lobbybalancer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by on 10-11.
 */
@Data
@EqualsAndHashCode(of = "pattern")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Zone {

    private final Pattern pattern;
    private final Map<String, Info> all = new ConcurrentHashMap<>();

    public void put(Info input) {
        all.put(input.getServerInfo().getName(), input);
    }

    public void remove(String name) {
        Info removal = all.remove(name);
        if (!$.nil(removal)) {
            ScheduledTask later = removal.getLater();
            if (!$.nil(later)) {
                later.cancel();
            }
        }
    }

    public Queue<String> alive() {
        PriorityQueue<Info> queue = new PriorityQueue<>(all.values());
        LinkedList<String> output = new LinkedList<>();
        for (Info info : queue) {
            if (!(info.getValue() == Integer.MAX_VALUE)) {
                output.add(info.getServerInfo().getName());
            }
        }
        return output;
    }

    public void update() {
        Map<String, ServerInfo> all = BungeeCord.getInstance().getServers();
        synchronized (all) {
            all.forEach((key, value) -> {
                if (!pattern.matcher(key).matches()) {
                    return;
                }
                InfoMgr.select(value).update(this);
            });
        }
    }

    static Zone build(@NonNull Pattern pattern) {
        return new Zone(pattern);
    }

}
