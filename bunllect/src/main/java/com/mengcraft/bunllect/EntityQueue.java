package com.mengcraft.bunllect;

import com.mengcraft.bunllect.entity.IEntity;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created on 16-7-28.
 */
public class EntityQueue extends LinkedBlockingQueue<IEntity> {
    public static final EntityQueue QUEUE = new EntityQueue();

    private EntityQueue() {
    }
}
