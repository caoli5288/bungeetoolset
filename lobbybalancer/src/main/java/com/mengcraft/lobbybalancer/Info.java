package com.mengcraft.lobbybalancer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by on 10-11.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Data
@EqualsAndHashCode(of = "serverInfo")
public class Info implements Comparable<Info> {

    private final ServerInfo serverInfo;
    private int value;

    @Setter(value = AccessLevel.NONE)
    private long updateTime;

    private transient ScheduledTask later;

    @Override
    public int compareTo(@NotNull Info ele) {
        if ($.isUseUpdater()) {
            return value - ele.value;
        }
        return serverInfo.getPlayers().size() - ele.serverInfo.getPlayers().size();
    }

    public int getValue() {
        if ($.isUseUpdater()) {
            return value;
        }
        return serverInfo.getPlayers().size();
    }

    public int incValue() {
        if ($.isUseUpdater()) {
            return ++value;
        }
        return serverInfo.getPlayers().size();
    }

    public int decValue() {
        if ($.isUseUpdater()) {
            return --value;
        }
        return serverInfo.getPlayers().size();
    }

    public void update(Zone zone) {
        if (!$.nil(zone)) {
            zone.put(this);
            ZoneMgr.register(zone, serverInfo);
        }

        if (!$.isUseUpdater() || $.now() - updateTime < 60000) {
            return;
        }

        updateTime = $.now();
        value = Integer.MAX_VALUE;

        serverInfo.ping((result, err) -> {
            if ($.nil(err)) {
                val i = result.getPlayers();
                value = i.getOnline() - i.getMax();
            } else {
                updateLater();
            }
        });
    }

    public void updateLater() {
        if (!$.isUseUpdater()) {
            return;
        }

        if (!$.nil(later)) {
            later.cancel();
        }

        later = BungeeCord.getInstance().getScheduler().schedule($.getPlugin(), () -> update(null), 15, TimeUnit.SECONDS);
    }

}
