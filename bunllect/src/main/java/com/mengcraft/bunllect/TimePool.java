package com.mengcraft.bunllect;

import com.mengcraft.bunllect.entity.EntityTotal;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by on 2017/6/11.
 */
public enum TimePool {

    INSTANCE;

    private final Map<UUID, Future<Pair<EntityTotal, DateValidObject<Integer>>>> pool = new HashMap<>();

    @SneakyThrows
    public Pair<EntityTotal, DateValidObject<Integer>> get(Player p) {
        return look(p).get();
    }

    private final String total = "SELECT" +
            " `life`,`latest_join` " +
            "FROM" +
            " `bunllect_total` " +
            "WHERE" +
            " `name` = ?" +
            ";";

    private final String between = "SELECT" +
            " `life`,`time` " +
            "FROM" +
            " `bunllect` " +
            "WHERE" +
            " `time` > ? " +
            "AND" +
            " `time` < ? " +
            "AND" +
            " `name` = ?" +
            ";";

    public Future<Pair<EntityTotal, DateValidObject<Integer>>> look(Player p) {
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
                AtomicInteger letch = new AtomicInteger();
                try (val st = conn.prepareStatement(between)) {
                    st.setString(1, LocalDate.now().toString());
                    st.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    st.setString(3, p.getName());
                    try (val result = st.executeQuery()) {
                        while (result.next()) {
                            int life = result.getInt("life");
                            Instant quit = result.getTimestamp("time").toInstant();
                            Instant join = quit.minusSeconds(life);
                            if (quit.atZone(ZoneId.systemDefault()).toLocalDate().isEqual(join.atZone(ZoneId.systemDefault()).toLocalDate())) {
                                letch.addAndGet(life);
                            } else {
                                letch.addAndGet((int) ChronoUnit.SECONDS.between(LocalDate.now().atStartOfDay(), LocalDateTime.now()));
                            }
                        }
                    }

                }
                return new Pair<>(t, new DateValidObject<>(letch.intValue()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new Pair<>(t, new DateValidObject<>(-1));
        }));
    }

    @SneakyThrows
    public static int between(Player p, Timestamp left, Timestamp right) {
        AtomicInteger letch = new AtomicInteger();
        try (val statement = MyPlugin.conn.getConnection().prepareStatement(INSTANCE.between)) {
            statement.setTimestamp(1, (left));
            statement.setTimestamp(2, (right));
            statement.setString(3, p.getName());
            try (val result = statement.executeQuery()) {
                while (result.next()) {
                    int life = result.getInt("life");
                    Instant quit = result.getTimestamp("time").toInstant();
                    Instant join = quit.minusSeconds(life);
                    if (quit.atZone(ZoneId.systemDefault()).toLocalDate().isEqual(join.atZone(ZoneId.systemDefault()).toLocalDate())) {
                        letch.addAndGet(life);
                    } else {
                        letch.addAndGet((int) ChronoUnit.SECONDS.between(quit.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(), quit.atZone(ZoneId.systemDefault()).toLocalDateTime()));
                    }
                }
            }
        }
        return letch.get();
    }

    public static void quit(Player p) {
        INSTANCE.pool.remove(p.getUniqueId());
    }

}
