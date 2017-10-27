package com.mengcraft.lobbybalancer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
    private int ref;
    private int value;

    @Override
    public int compareTo(@NonNull Info other) {
        return value - other.value;
    }

    public int incValue() {
        return ++value;
    }

    public void update(Runnable callback) {
        value = Integer.MAX_VALUE;
        serverInfo.ping((result, err) -> {
            if (err == null) {
                val cnt = result.getPlayers();
                value = cnt.getOnline() - cnt.getMax();
            }
            if (!(callback == null)) callback.run();
        });
        ref++;
    }

    static Info bind(ServerInfo info) {
        return new Info(info);
    }
}
