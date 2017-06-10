package com.mengcraft.bunllect;

import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by on 2017/6/11.
 */
public enum TimePool {

    INSTANCE;

    private final Map<UUID, Future<Integer>> pool = new HashMap<>();

    @SneakyThrows
    public Integer get(Player p) {
        return look(p).get();
    }

    public Future<Integer> look(Player p) {
        return pool.computeIfAbsent(p.getUniqueId(), id -> CompletableFuture.supplyAsync(() -> {
            try {
                val conn = MyPlugin.conn.getConnection();
                try (val statement = conn.createStatement()) {
                    try (val result = statement.executeQuery("select life from bunllect_total where name = '" + p.getName() + "';")) {
                        if (result.next()) return result.getInt("life");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        }));
    }

}
