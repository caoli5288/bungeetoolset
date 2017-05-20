package com.mengcraft.bunllect.entity;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 17-4-25.
 */
public interface IEntity {

    void update(Statement statement) throws SQLException;

    boolean valid();
}
