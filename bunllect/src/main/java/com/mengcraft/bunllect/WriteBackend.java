package com.mengcraft.bunllect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created on 16-5-30.
 */
public class WriteBackend implements Runnable {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private ConnectionFactory factory;
    private boolean shutdown;

    @Override
    public void run() {
        while (!shutdown) {
            try {
                String take = queue.take();
                Connection connection = factory.getConnection();
                try (Statement st = connection.createStatement()) {
                    st.execute(take);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException | ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addBatch(String j) {
        queue.offer(j);
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public void setFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

}
