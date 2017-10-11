package com.mengcraft.lobbybalancer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Created by on 10-11.
 */
@Data
@EqualsAndHashCode(of = "pattern")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Zone {

    private final Pattern pattern;
    private final BlockingQueue<Info> queue = new PriorityBlockingQueue<>();

    @SneakyThrows
    public void connect(ProxiedPlayer p, Consumer<Boolean> callback) {
        Info poll = queue.poll(1, TimeUnit.MINUTES);
        if (poll.getValue() == Integer.MAX_VALUE) {
            poll.update(() -> queue.add(poll));
            if (!(callback == null)) callback.accept(false);
        } else {
            p.connect(poll.getServerInfo(), (result, err) -> {
                if (result) {
                    if (poll.incValue() == 0) {
                        poll.update(() -> queue.add(poll));
                    } else {
                        queue.add(poll);
                    }
                    if (!(callback == null)) callback.accept(true);
                } else {
                    poll.update(() -> queue.add(poll));
                    if (p.isConnected()) connect(p, callback);
                }
            });
        }
    }

    public int size() {
        return queue.size();
    }

    static Zone build(Pattern pattern) {
        Zone zone = new Zone(pattern);
        BungeeCord.getInstance().getServers().forEach((name, serverInfo) -> {
            if (pattern.matcher(name).matches()) {
                Info info = InfoMgr.INST.get(serverInfo);
                if (info.getRef() == 0 || info.getValue() == Integer.MAX_VALUE) {
                    info.update(() -> zone.queue.add(info));
                } else {
                    zone.queue.add(info);
                }
            }
        });
        return zone;
    }

}
