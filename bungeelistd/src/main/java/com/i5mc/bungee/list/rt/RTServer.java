package com.i5mc.bungee.list.rt;

import com.i5mc.bungee.list.rt.protocol.Dist;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created on 17-7-19.
 */
public enum RTServer implements Runnable {

    INSTANCE;

    private ServerSocket socket;
    private Logger log;
    private ScheduledTask t;
    private ExecutorService pool;
    private RTDiscover discover;

    public static void init(ExecutorService pool, Logger log, ScheduledTask t) {
        INSTANCE.log = log;
        INSTANCE.pool = pool;
        INSTANCE.t = t;
        pool.execute(INSTANCE);
        val cover = RT.INSTANCE.getDiscover();
        if (!(cover == null)) {
            val discover = new RTDiscover(cover);
            try {
                if (discover.init()) {
                    discover.subscribe(pool);
                    INSTANCE.discover = discover;
                    log("Discover service okay");
                }
            } catch (IOException e) {
                log(e);
            }
        }
    }

    @SneakyThrows
    public void run() {
        socket = new ServerSocket();
        socket.bind(new InetSocketAddress(RT.PORT));
        while (!socket.isClosed()) {
            try (val i = socket.accept()) {
                exec(() -> {
                    log(">>> " + i.getInetAddress().getHostAddress());
                    try {
                        Protocol.input(i.getInputStream()).exec(i);
                    } catch (IOException ign) {
                        ;
                    }
                });
            } catch (IOException ign) {
                ;
            } catch (Exception ign) {
                log.log(Level.SEVERE, ign.toString(), ign);
            }
        }
    }

    public static void log(Object line) {
        log(line, false);
    }

    public static void log(Object line, boolean force) {
        if (force || RT.INSTANCE.isLog()) {
            INSTANCE.log.info("" + line);
        }
    }

    public static void exec(Runnable r) {
        INSTANCE.pool.execute(r);
    }

    @SneakyThrows
    public static void close() {
        val i = INSTANCE;
        if (!(i.discover == null)) i.discover.unsubscribe();
        i.t.cancel();
        if (!(i.socket == null || i.socket.isClosed())) i.socket.close();
    }

    public static void distribute(Dist out) {
        val discover = INSTANCE.discover;
        if (discover == null) {
            for (val to : RT.INSTANCE.getDist()) {
                exec(() -> {
                    try (val cli = new Socket()) {
                        cli.connect(new InetSocketAddress(to, RT.PORT), 4000);
                        Protocol.output(cli.getOutputStream(), out);
                    } catch (IOException ign) {
                    }
                });
            }
        } else {
            val buf = new ByteArrayOutputStream();
            Protocol.output(buf, out);
            try (val cli = discover.getPool().getResource()) {
                cli.publish(RTDiscover.PUB.getBytes("UTF-8"), buf.toByteArray());
            } catch (IOException ign) {
            }
        }
        log(out);
    }

}
