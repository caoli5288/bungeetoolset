package com.mengcraft.bunllect;

import com.mengcraft.bunllect.entity.EntityTotal;
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

    private final Map<UUID, Future<Pair<EntityTotal, Integer>>> pool = new HashMap<>();

    @SneakyThrows
    public Pair<EntityTotal, Integer> get(Player p) {
        return look(p).get();
    }

    private final String total = "SELECT" +
            " `life`,`latest_join` " +
            "FROM" +
            " `bunllect_total` " +
            "WHERE" +
            " `name` = ?" +
            ";";

    private final String today = "SELECT" +
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

    public Future<Pair<EntityTotal, Integer>> look(Player p) {
        return pool.computeIfAbsent(p.getUniqueId(), id -> CompletableFuture.supplyAsync(() -> {
            EntityTotal t = new EntityTotal();
            try {
                val conn = MyPlugin.conn.getConnection();
                try (val st = conn.prepareStatement(this.total)) {
                    st.setString(1, p.getName());
                    try (val result = st.executeQuery()) {
                        if (result.next()) {
                            t.setLife(result.getInt("life"));
                            t.setLatestJoin(result.getTimestamp("latest_join"));
                        }
                    }
                }
                int i2 = -1;
                try (val st = conn.prepareStatement(today)) {
                    st.setString(1, $.now().toString().substring(0, 10));
                    st.setString(2, p.getName());
                    try (val result = st.executeQuery()) {
                        if (result.next()) i2 = result.getInt("i2");
                    }

                }
                return new Pair<>(t, i2);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new Pair<>(t, -1);
        }));
    }

    public static void quit(Player p) {
        INSTANCE.pool.remove(p.getUniqueId());
    }

}
