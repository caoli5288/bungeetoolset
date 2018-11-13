package com.mengcraft.lobbybalancer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Created by on 10-11.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Data
@EqualsAndHashCode(of = "serverInfo")
public class Info implements Comparable<Info> {

    private static final AtomicIntegerFieldUpdater<Info> VALUE_UPDATE = AtomicIntegerFieldUpdater.newUpdater(Info.class, "value");
    private final ServerInfo serverInfo;
    volatile int value;

    @Setter(value = AccessLevel.NONE)
    private long updateTime;

    private volatile ScheduledTask scheduledUpdate;

    @Override
    public int compareTo(@NonNull Info ele) {
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
            return VALUE_UPDATE.incrementAndGet(this);
        }
        return serverInfo.getPlayers().size();
    }

    public int decValue() {
        if ($.isUseUpdater()) {
            return VALUE_UPDATE.decrementAndGet(this);
        }
        return serverInfo.getPlayers().size();
    }

    public void update(Zone zone) {
        scheduledUpdate = null;

        if (!$.nil(zone)) {
            ZoneMgr.register(zone, this);
        }

        if (!$.isUseUpdater()) {
            return;
        }

        if ($.now() - updateTime < 60000 && value != Integer.MAX_VALUE) {// Always ping if previous failure.
            return;
        }

        updateTime = $.now();
        value = Integer.MAX_VALUE;

        serverInfo.ping((result, err) -> {
            if ($.nil(err)) {
                val i = result.getPlayers();
                value = i.getOnline() - i.getMax();
            } else {
                updateLater(zone);
            }
        });
    }

    public void updateLater(Zone zone) {
        if (!$.isUseUpdater() || scheduledUpdate != null) {
            return;
        }

        scheduledUpdate = BungeeCord.getInstance().getScheduler().schedule($.getPlugin(), () -> update(zone), 15, TimeUnit.SECONDS);
    }

}
