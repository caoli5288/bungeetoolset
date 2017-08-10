package com.i5mc.bungee.list.rt;

import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
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

    public static void init(ExecutorService pool, Logger log, ScheduledTask t) {
        INSTANCE.log = log;
        INSTANCE.pool = pool;
        INSTANCE.t = t;
        pool.execute(INSTANCE);
    }

    @SneakyThrows
    public void run() {
        socket = new ServerSocket();
        socket.bind(new InetSocketAddress(RT.PORT));
        while (!socket.isClosed()) {
            val i = socket.accept();
            exec(() -> {
                log(">>> " + i.getInetAddress().getHostAddress());
                try (val cli = i) {
                    Protocol.input(cli).exec(i);
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.toString(), e);
                }
            });
        }
    }

    public static void log(Object line) {
        if (RT.INSTANCE.isLog()) {
            INSTANCE.log.info("" + line);
        }
    }

    public static void exec(Runnable r) {
        INSTANCE.pool.execute(r);
    }

    public static boolean isClosed() {
        val i = INSTANCE;
        return i.socket == null || i.socket.isClosed();
    }

    @SneakyThrows
    public static void close() {
        val i = INSTANCE;
        i.t.cancel();
        i.socket.close();
    }

}
