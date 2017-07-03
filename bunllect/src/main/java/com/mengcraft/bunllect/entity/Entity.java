package com.mengcraft.bunllect.entity;

import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 16-7-27.
 */
@AllArgsConstructor
public class Entity implements IEntity {

    private String name;
    private String ip;
    private int life;
    private String instance;
    private String host;
    private String hostIp;

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
                " host_ip = '" + hostIp + "'," +
                " host = '" + host + "'" +
                ";");
    }

}
