package com.mengcraft.bunllect;

import com.mengcraft.bunllect.entity.EntityTotal;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created on 17-4-25.
 */
public class PAPIHook extends EZPlaceholderHook {

    public PAPIHook(Plugin plugin) {
        super(plugin, "bunllect");
    }

    interface IExec {

        String exec(Player p, Iterator<String> itr);
    }

    enum Lab implements IExec {

        ONTIME((p, itr) -> {
            EntityTotal total = TimePool.INSTANCE.get(p).getLeft();
            long time = total.getLife() + ChronoUnit.SECONDS.between(total.getLatestJoin().toInstant(), Instant.now());
            if (time > 1) return TimeHelper.parse(time);
            return "无数据";
        }),

        TIME((p, itr) -> {
            EntityTotal total = TimePool.INSTANCE.get(p).getLeft();
            return String.valueOf(total.getLife() + ChronoUnit.SECONDS.between(total.getLatestJoin().toInstant(), Instant.now()));
        }),

        TODAY((p, itr) -> {
            Pair<EntityTotal, DateValidObject<Integer>> pair = TimePool.INSTANCE.get(p);
            Integer join = pair.getRight().get();
            if (join == null) {// 从零点起未从spigot端口下线
                return "" + ChronoUnit.SECONDS.between(LocalDate.now().atStartOfDay(), LocalDateTime.now());
            }
            LocalDateTime latestJoin = pair.getLeft().getLatestJoin().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (latestJoin.toLocalDate().isBefore(LocalDate.now())) {// 从零点起未从bc端口下线
                return "" + ChronoUnit.SECONDS.between(LocalDate.now().atStartOfDay(), LocalDateTime.now());
            }
            return String.valueOf(join + ChronoUnit.SECONDS.between(latestJoin, LocalDateTime.now()));
        }),

        BETWEEN((p, itr) -> {
            LocalDate left = LocalDate.parse(itr.next());
            LocalDate right = itr.hasNext() ? LocalDate.parse(itr.next()) : LocalDate.now();
            Integer pull = L2Pool.pull(p.getName() + ":" + left + ":" + right, () -> TimePool.between(p, toTimestamp(left), toTimestamp(right)));
            return pull == null ? "-1" : String.valueOf(pull);
        });

        final IExec i;

        Lab(IExec i) {
            this.i = i;
        }

        @Override
        public String exec(Player p, Iterator<String> itr) {
            return i.exec(p, itr);
        }
    }

    @Override
    public String onPlaceholderRequest(Player p, String label) {
        Iterator<String> itr = Arrays.asList(label.split("_")).iterator();
        return Lab.valueOf(itr.next().toUpperCase()).exec(p, itr);
    }

    static Timestamp toTimestamp(LocalDate input) {
        return Timestamp.from(input.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

}
