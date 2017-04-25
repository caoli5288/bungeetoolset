package com.mengcraft.bunllect;

import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created on 17-4-25.
 */
public class MyPlugin extends JavaPlugin {

    static class Hold {
        public static final Map<UUID, CompletableFuture<Integer>> H = new HashMap<>();
    }

    static ConnectionFactory conn;

    @Override
    public void onEnable() {
        val file = new File(getDataFolder(), "bunllect.conf");
        if (!file.exists()) {
            saveResource("bunllect.conf", false);
        }

        val p = new Properties();
        try {
            p.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        conn = new ConnectionFactory();
        conn.setDriver(p.getProperty("bunllect.jdbc.driver"));
        conn.setUrl(p.getProperty("bunllect.jdbc.url"));
        conn.setUser(p.getProperty("bunllect.jdbc.user"));
        conn.setPassword(p.getProperty("bunllect.jdbc.password"));

        val query = new Query(this);
        query.hook();
    }
}
