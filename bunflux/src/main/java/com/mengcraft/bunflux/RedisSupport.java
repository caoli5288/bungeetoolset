package com.mengcraft.bunflux;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

/**
 * Created on 16-5-30.
 */
public class RedisSupport implements Runnable {

    private final RedisBungeeAPI api = RedisBungee.getApi();
    private final Main main;

    public RedisSupport(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        main.getProxy().getScheduler().runAsync(main, () -> main.getInflux().write("player_value")
                .where("server", main.getServer())
                .value("value", api.getPlayerCount())
                .flush()
        );
    }
}
