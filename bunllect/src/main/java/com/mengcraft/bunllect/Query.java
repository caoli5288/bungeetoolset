package com.mengcraft.bunllect;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created on 17-4-25.
 */
public class Query extends EZPlaceholderHook {

    public Query(Plugin plugin) {
        super(plugin, "bunllect");
    }

    interface IExec {

        String exec(Player p);
    }

    enum Lab implements IExec {

        ONTIME(p -> {
            int time = TimePool.INSTANCE.get(p);
            if (time > 1) return TimeHelper.parse(time);
            return "无数据";
        }),

        TIME(p -> "" + TimePool.INSTANCE.get(p));

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
