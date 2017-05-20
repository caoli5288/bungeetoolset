package com.mengcraft.bunllect.entity;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 16-7-27.
 */
public class Entity implements IEntity {
    private String name;
    private String ip;
    private int life;
    private String instance;
    private String host;

    public Entity(String name, String ip, int life, String instance, String host) {
        this.name = name;
        this.ip = ip;
        this.life = life;
        this.instance = instance;
        this.host = host;
    }

    public boolean valid() {
        return life > 60;
    }

    @Override
    public void update(Statement statement) throws SQLException {
        statement.executeUpdate("INSERT " +
                "INTO" +
                " bunllect " +
                "SET" +
                " name = '" + name + "'," +
                " ip = '" + ip + "'," +
                " life = " + life + "," +
                " instance = '" + instance + "'," +
                " host = '" + host + "'" +
                ";");
    }

}
