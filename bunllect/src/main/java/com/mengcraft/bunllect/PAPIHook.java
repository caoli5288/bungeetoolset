package com.mengcraft.bunllect;

import com.mengcraft.bunllect.entity.EntityTotal;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Created on 17-4-25.
 */
public class PAPIHook extends EZPlaceholderHook {

    public PAPIHook(Plugin plugin) {
        super(plugin, "bunllect");
    }

    interface IExec {

        String exec(Player p);
    }

    enum Lab implements IExec {

        ONTIME(p -> {
            EntityTotal total = TimePool.INSTANCE.get(p).getLeft();
            long time = total.getLife() + ChronoUnit.SECONDS.between(total.getLatestJoin().toInstant(), Instant.now());
            if (time > 1) return TimeHelper.parse(time);
            return "无数据";
        }),

        TIME(p -> {
            EntityTotal total = TimePool.INSTANCE.get(p).getLeft();
            return String.valueOf(total.getLife() + ChronoUnit.SECONDS.between(total.getLatestJoin().toInstant(), Instant.now()));
        }),

        TODAY(p -> {
            Pair<EntityTotal, Integer> pair = TimePool.INSTANCE.get(p);
            return String.valueOf(ChronoUnit.SECONDS.between(pair.getLeft().getLatestJoin().toInstant(), Instant.now()) + pair.getRight());
        });

        final IExec i;

        Lab(IExec i) {
            this.i = i;
        }

        @Override
        public String exec(Player p) {
            return i.exec(p);
        }
    }

    @Override
    public String onPlaceholderRequest(Player p, String label) {
        return Lab.valueOf(label.toUpperCase()).exec(p);
    }

}
