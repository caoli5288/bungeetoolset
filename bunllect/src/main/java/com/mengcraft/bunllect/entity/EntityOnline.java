package com.mengcraft.bunllect.entity;

import lombok.val;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by on 2017/5/20.
 */
public class EntityOnline implements IEntity {

    private static EntityOnline next;

    private final String host;
    private final int count;
    private final int throttle;

    private EntityOnline last;
    private int wave;

    private EntityOnline(String host, int count, int throttle) {
        this.host = host;
        this.count = count;
        this.throttle = throttle;
    }

    @Override
    public void update(Statement statement) throws SQLException {
        statement.execute("insert into bunllect_online set host = '" + host +
                "', online = " + count +
                ", wave = " + wave +
                ", updated = now();");
    }

    @Override
    public boolean valid() {
        return !(last == null) && last.count > 0 && Math.abs(wave = count - last.count) > throttle;
    }

    public static EntityOnline build(String host, int count, int throttle) {
        val out = new EntityOnline(host, count, throttle);
        if (!(next == null)) out.last = next;
        next = out;
        return out;
    }

}
