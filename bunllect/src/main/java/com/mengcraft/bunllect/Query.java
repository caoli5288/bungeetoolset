package com.mengcraft.bunllect;

import lombok.Cleanup;
import lombok.val;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created on 17-4-25.
 */
public class Query extends EZPlaceholderHook {

    public Query(Plugin plugin) {
        super(plugin, "bunllect");
    }

    interface IExec {

        String exec(Player p, Iterator<String> input);
    }

    static String ontime(Player p, Iterator<String> input) {
        val h = MyPlugin.Hold.H;
        val i = p.getUniqueId();
        val query = h.get(i);
        if (query == null) {
            h.put(i, CompletableFuture.supplyAsync(() -> {
                try {
                    val conn = MyPlugin.conn.getConnection();
                    @Cleanup val statement = conn.createStatement();
                    @Cleanup val result = statement.executeQuery("select life from bunllect_total where name = '" + p.getName() + "';");
                    if (result.next()) {
                        return result.getInt("life");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return -1;
            }));
            return "查询中";
        }
        if (!query.isDone()) return "查询中";
        try {
            val result = query.get();
            if (result > -1) {
                return TimeHelper.parse(result);
            } else {
                return "没有数据";
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "查询出现问题请联系管理员";
    }

    enum E implements IExec {
        ONTIME(Query::ontime);

        final IExec i;

        E(IExec i) {
            this.i = i;
        }

        @Override
        public String exec(Player p, Iterator<String> input) {
            return i.exec(p, input);
        }
    }

    @Override
    public String onPlaceholderRequest(Player p, String label) {
        val input = Arrays.asList(label.split("_")).iterator();
        return E.valueOf(input.next().toUpperCase()).exec(p, input);
    }

}
