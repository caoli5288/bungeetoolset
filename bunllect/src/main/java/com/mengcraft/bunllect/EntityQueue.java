package com.mengcraft.bunllect;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created on 16-7-28.
 */
public class EntityQueue extends LinkedBlockingQueue<Entity> {
    public static final EntityQueue QUEUE = new EntityQueue();

    private EntityQueue() {
    }
}
