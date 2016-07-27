package com.mengcraft.bunllect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 16-5-30.
 */
public class WriteBackend implements Runnable {

    private final static String PRE_COMMAND = "INSERT " +
            "INTO" +
            " bunllect " +
            "SET" +
            " name = ?," +
            " ip = ?," +
            " life = ?," +
            " instance = ?," +
            " host = ?" +
            ";";

    private final EntityQueue queue = EntityQueue.QUEUE;
    private final ConnectionFactory factory;
    private final Main main;

    public WriteBackend(Main main, ConnectionFactory factory) {
        this.main = main;
        this.factory = factory;
    }

    @Override
    public void run() {
        while (!main.shutdown) {
            processQueue();
        }
    }

    private void processQueue() {
        try {
            Entity taked = queue.take();
            if (taked.valid()) {
                processEntity(taked);
            }
        } catch (InterruptedException ignore) {
        }
    }

    private void processEntity(Entity entity) {
        try (Connection conn = factory.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(PRE_COMMAND)) {
                st.setString(1, entity.getName());
                st.setString(2, entity.getIp());
                st.setInt(3, entity.getLife());
                st.setString(4, entity.getInstance());
                st.setString(5, entity.getHost());
                st.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
