package com.mengcraft.bunllect.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created on 17-4-25.
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class EntityTotal implements IEntity {

    private String name;
    private UUID id;
    private String latestIp;
    private int life;
    private Timestamp latestJoin;
    private transient boolean join;

    @Override
    public boolean valid() {
        return join || life > 60;
    }

    @Override
    public void update(Statement statement) throws SQLException {
        if (join) {
            int row = statement.executeUpdate("update bunllect_total set latest_join = now() where name = '" + name +
                    "';");
            if (row < 1) {
                statement.executeUpdate("insert into bunllect_total set name = '" + name +
                        "', uuid = '" + id +
                        "', latest_join = now()" +
                        ";");
            }
        } else {
            int row = statement.executeUpdate("update bunllect_total set latest_ip = '" + latestIp +
                    "', life = life + " + life + ", latest_quit = now() where name = '" + name +
                    "';");
            if (row == 0) {
                statement.executeUpdate("insert into bunllect_total set name = '" + name +
                        "', uuid = '" + id +
                        "', latest_ip = '" + latestIp +
                        "', life = " + life + ", latest_quit = now();");
            }
        }
    }

}
