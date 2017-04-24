package com.mengcraft.bunllect;

import lombok.AllArgsConstructor;
import lombok.Setter;

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
    public String toString() {
        return ("replace into bunllect_total set name = '" + name +
                "', uuid = '" + uuid.toString() +
                "', latest_ip = '" + latestIp +
                "', life = life + " + life +
                " latest_quit = now() where name = '" + name +
                "';");
    }

}
