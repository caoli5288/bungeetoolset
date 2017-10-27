package com.mengcraft.lobbybalancer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * Created by on 10-11.
 */
@Data
@EqualsAndHashCode(of = "serverInfo")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Info implements Comparable<Info> {

    private final ServerInfo serverInfo;
    private int value;

    @Setter(value = AccessLevel.NONE)
    private long updateTime;

    @Override
    public int compareTo(@NonNull Info other) {
        return value - other.value;
    }

    public int incValue() {
        return ++value;
    }

    public boolean outdated() {
        return $.now() - updateTime > 60000;
    }

    public void update(Runnable callback) {
        value = Integer.MAX_VALUE;
        updateTime = $.now();
        serverInfo.ping((result, err) -> {
            if (err == null) {
                val cnt = result.getPlayers();
                value = cnt.getOnline() - cnt.getMax();
            }
            if (!(callback == null)) callback.run();
        });
    }

    static Info bind(ServerInfo info) {
        return new Info(info);
    }
}
