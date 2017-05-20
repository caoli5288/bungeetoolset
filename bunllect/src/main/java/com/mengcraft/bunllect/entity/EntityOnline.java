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

    private EntityOnline last;
    private int wave;

    private EntityOnline(String host, int count) {
        this.host = host;
        this.count = count;
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
        return last == null || (wave = Math.abs(last.count - count)) > 50;
    }

    public static EntityOnline build(String host, int count) {
        val out = new EntityOnline(host, count);
        if (!(next == null)) out.last = next;
        next = out;
        return out;
    }

}
