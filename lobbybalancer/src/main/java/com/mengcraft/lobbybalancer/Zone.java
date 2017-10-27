package com.mengcraft.lobbybalancer;

import lombok.*;
import net.md_5.bungee.BungeeCord;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
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

    public void queue(Info add) {
        while (queue.remove(add)) {
            ;
        }
        queue.add(add);
    }

    public Queue<String> alive() {
        val itr = new PriorityQueue<Info>(queue);
        val out = new LinkedList<String>();
        while (!itr.isEmpty()) {
            Info info = itr.poll();
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
                Info info = InfoMgr.INST.mapping(serverInfo);
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
