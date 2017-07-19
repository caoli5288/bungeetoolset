package com.i5mc.bungee.list.rt;

import lombok.SneakyThrows;
import lombok.val;

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
    private ServerSocket socket;

    public void init(Logger log, ExecutorService pool, List<String> dist) {
        this.log = log;
        this.pool = pool;
        this.dist = dist;
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
        INSTANCE.socket.close();
    }

    public static List<String> getDist() {
        return INSTANCE.dist;
    }

}
