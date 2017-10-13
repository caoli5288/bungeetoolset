package com.i5mc.bungee.list.rt;

import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by on 2017/10/10.
 */
@Data
public class RTDiscover extends BinaryJedisPubSub {

    public static final String PUB = "bungeelistd.pub";

    private final RT.Discover discover;
    private JedisPool pool;
    private String localhost;

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        val buf = new ByteArrayInputStream(message);
        Protocol.input(buf).exec(null);
    }

    public boolean init() throws IOException {
        val host = discover.getHost();
        if (host == null || host.isEmpty()) return false;

        pool = new JedisPool(host, discover.getPort());
        try (val cli = pool.getResource()) {
            localhost = cli.getClient().getSocket().getLocalAddress().getHostAddress();
        }
        return true;
    }

    /**
     * BungeeCord side only.
     *
     * @param th the executor service
     */
    @SneakyThrows
    public void subscribe(@NonNull ExecutorService th) {
        byte[] ch = PUB.getBytes("UTF-8");
        th.execute(() -> {
            try (val cli = pool.getResource()) {
                cli.subscribe(this, ch);
            }
        });
    }

}
