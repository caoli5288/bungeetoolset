package com.mengcraft.bunllect;

import lombok.val;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created on 17-4-25.
 */
public class MyPlugin extends JavaPlugin implements Listener, EventExecutor {

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

        val query = new PAPIHook(this);
        query.hook();

        PlayerQuitEvent.getHandlerList().register(new RegisteredListener(this, this, EventPriority.NORMAL, this, false));
    }

    @Override
    public void execute(Listener l, Event event) throws EventException {
        val evt = ((PlayerQuitEvent) event);
        TimePool.quit(evt.getPlayer());
    }
}
