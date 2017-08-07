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

    private final Map<UUID, Future<Pair<Integer, Integer>>> pool = new HashMap<>();

    @SneakyThrows
    public Pair<Integer, Integer> get(Player p) {
        return look(p).get();
    }

    static final String TOTAL = "SELECT" +
            " `life` " +
            "FROM" +
            " `bunllect_total` " +
            "WHERE" +
            " `name` = ?" +
            ";";

    static final String TODAY = "SELECT" +
            " SUM(`life`) AS `i2` " +
            "FROM" +
            " `bunllect` " +
            "WHERE" +
            " `time` > ? " +
            "AND" +
            " `time` < NOW() " +
            "AND" +
            " `name` = ?" +
            ";";

    public Future<Pair<Integer, Integer>> look(Player p) {
        return pool.computeIfAbsent(p.getUniqueId(), id -> CompletableFuture.supplyAsync(() -> {
            try {
                int i1 = -1;
                val conn = MyPlugin.conn.getConnection();
                try (val st = conn.prepareStatement(TOTAL)) {
                    st.setString(1, p.getName());
                    try (val result = st.executeQuery()) {
                        if (result.next()) i1 = result.getInt("life");
                    }
                }
                int i2 = -1;
                try (val st = conn.prepareStatement(TODAY)) {
                    st.setString(1, $.now().toString().substring(0, 10));
                    st.setString(2, p.getName());
                    try (val result = st.executeQuery()) {
                        if (result.next()) i2 = result.getInt("i2");
                    }

                }
                return new Pair<>(i1, i2);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new Pair<>(-1, -1);
        }));
    }

    public static void quit(Player p) {
        INSTANCE.pool.remove(p.getUniqueId());
    }

}
