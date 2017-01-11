package com.mengcraft.slashlimit;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 15-12-23.
 */
public class Limiter<T> {

    private final Map<T, Entity> map = new ConcurrentHashMap<>();
    private final int count;
    private final long time;
    private final int border;

    public Limiter(long time, int count, int border) {
        this.count = count;
        this.time = time;
        this.border = border;
    }

    public Limiter(long time, int count) {
        this(time, count, Integer.MAX_VALUE);
    }

    public Limiter(long time) {
        this(time, 1, Integer.MAX_VALUE);
    }

    public boolean valid(T object) {
        if (map.size() == border) {
            reduce();
        }
        Entity entity = map.get(object);
        if (entity == null) {
            map.put(object, entity = new Entity());
        }
        long now = System.currentTimeMillis();
        if (entity.time + time < now) {
            entity.time = now;
            entity.count = 0;
        }
        return entity.count++ < count;
    }

    private void reduce() {
        Iterator<Entity> it = map.values().iterator();
        for (long now = System.currentTimeMillis(); it.hasNext(); ) {
            if (it.next().time + time < now) {
                it.remove();
            }
        }
    }

    public static class Entity {

        private int count;
        private long time;
    }

}
