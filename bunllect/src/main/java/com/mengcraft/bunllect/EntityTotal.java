package com.mengcraft.bunllect;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created on 17-4-25.
 */
@Setter
@AllArgsConstructor
public class EntityTotal implements IEntity {

    private String name;
    private UUID uuid;
    private String latestIp;
    private int life;

    @Override
    public boolean valid() {
        return life > 60;
    }

    @Override
    public void update(Statement statement) throws SQLException {
        int row = statement.executeUpdate("update bunllect_total set latest_ip = '" + latestIp +
                "', life = life + " + life + ", latest_quit = now() where name = '" + name +
                "';");
        if (row == 0) {
            statement.executeUpdate("insert into bunllect_total set name = '" + name +
                    "', uuid = '" + uuid +
                    "', latest_ip = '" + latestIp +
                    "', life = " + life + ", latest_quit = now();");
        }
    }

}
