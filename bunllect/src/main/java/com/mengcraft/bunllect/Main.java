package com.mengcraft.bunllect;

import com.mengcraft.bunllect.entity.EntityOnline;
import com.mengcraft.bunllect.entity.IEntity;
import lombok.val;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created on 16-5-30.
 */
public class Main extends Plugin {

    public boolean shutdown;

    @Override
    public void onEnable() {
        File file = new File(getDataFolder(), "bunllect.conf");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (InputStream in = getResourceAsStream("bunllect.conf")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Properties p = new Properties();
        try {
            p.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setDriver(p.getProperty("bunllect.jdbc.driver"));
        factory.setUrl(p.getProperty("bunllect.jdbc.url"));
        factory.setUser(p.getProperty("bunllect.jdbc.user"));
        factory.setPassword(p.getProperty("bunllect.jdbc.password"));

        WriteBackend backend = new WriteBackend(this, factory);

        getProxy().getScheduler().runAsync(this, backend);

        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignore) {
            host = "";
        }

        val h = host;

        getProxy().getScheduler().schedule(this, () -> {
            EntityQueue.QUEUE.offer(EntityOnline.build(h, getProxy().getOnlineCount()));
        }, 10, 10, TimeUnit.SECONDS);

        getProxy().getPluginManager().registerListener(this, new Executor(h));
    }

    @Override
    public void onDisable() {
        shutdown = true;// To interrupt blocking.
        EntityQueue.QUEUE.offer(new IEntity() {
            public void update(Statement i) {
            }

            public boolean valid() {
                return false;
            }
        });
    }
}
