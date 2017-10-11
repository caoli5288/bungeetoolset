package com.mengcraft.lobbybalancer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    public void connect(ProxiedPlayer p, Consumer<Boolean> callback, AtomicInteger cnt) {
        Info poll = queue.poll(1, TimeUnit.MINUTES);
        if (poll.getValue() == Integer.MAX_VALUE) {
            poll.update(() -> queue(poll));
            if (!(callback == null)) callback.accept(false);
        } else {
            p.connect(poll.getServerInfo(), (result, err) -> {
                if (result) {
                    if (poll.incValue() >= -1) {
                        poll.update(() -> queue(poll));
                    } else {
                        queue(poll);
                    }
                    if (!(callback == null)) callback.accept(true);
                } else {
                    poll.update(() -> queue(poll));
                    if (p.isConnected() && !(cnt.decrementAndGet() == -1)) connect(p, callback, cnt);
                }
            });
        }
    }

    public void connect(ProxiedPlayer p, Consumer<Boolean> callback) {
        connect(p, callback, new AtomicInteger(size()));
    }

    public Info head() {
        return queue.peek();
    }

    public void queue(Info add) {
        while (queue.remove(add)) {
            ;
        }
        queue.add(add);
    }

    public int size() {
        return queue.size();
    }

    public Queue<String> alive() {
        LinkedList<String> out = new LinkedList<>();
        for (val info : queue) {
            if (!(info.getValue() == Integer.MAX_VALUE)) {
                out.add(info.getServerInfo().getName());
            }
        }
        return out;
    }

    static Zone build(@NonNull Pattern pattern) {
        Zone zone = new Zone(pattern);
        BungeeCord.getInstance().getServers().forEach((name, serverInfo) -> {
            if (pattern.matcher(name).matches()) {
                Info info = InfoMgr.INST.get(serverInfo);
                if (info.getRef() == 0 || info.getValue() == Integer.MAX_VALUE) {
                    info.update(() -> zone.queue(info));
                } else {
                    zone.queue(info);
                }
            }
        });
        return zone;
    }

    public static final Zone NIL = new Zone(null);
}
