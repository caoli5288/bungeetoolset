package com.i5mc.bungee.list.rt;

import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created on 17-7-19.
 */
public enum RTServer implements Runnable {

    INSTANCE;

    private Logger log;
    private ExecutorService pool;
    private List<String> dist;
    private ScheduledTask tsk;
    private ServerSocket socket;

    public void init(Logger log, ExecutorService pool, List<String> dist, ScheduledTask tsk) {
        this.log = log;
        this.pool = pool;
        this.dist = dist;
        this.tsk = tsk;
        pool.execute(this);
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
                    Protocol.input(cli);
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.toString(), e);
                }
                log("<<<");
            });
        }
    }

    public void setDist(List<String> dist) {
        this.dist = dist;
    }

    public static void log(Object line) {
        INSTANCE.log.info("" + line);
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
        i.tsk.cancel();
        i.socket.close();
    }

    public static List<String> getDist() {
        return INSTANCE.dist;
    }

}
