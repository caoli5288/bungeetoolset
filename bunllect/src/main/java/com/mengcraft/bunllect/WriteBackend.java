package com.mengcraft.bunllect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 16-5-30.
 */
public class WriteBackend implements Runnable {

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
            IEntity entity = queue.take();
            if (entity.valid()) {
                processEntity(entity);
            }
        } catch (InterruptedException ignore) {
        }
    }

    private void processEntity(IEntity entity) {
        try (Connection conn = factory.getConnection()) {
            try (Statement statement = conn.createStatement()) {
                entity.update(statement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
