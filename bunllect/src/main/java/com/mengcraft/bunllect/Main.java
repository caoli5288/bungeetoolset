package com.mengcraft.bunllect;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Properties;

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

        Properties conf = new Properties();
        try {
            conf.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setDriver(conf.getProperty("bunllect.jdbc.driver"));
        factory.setUrl(conf.getProperty("bunllect.jdbc.url"));
        factory.setUser(conf.getProperty("bunllect.jdbc.user"));
        factory.setPassword(conf.getProperty("bunllect.jdbc.password"));

        WriteBackend backend = new WriteBackend(this, factory);

        getProxy().getScheduler().runAsync(this, backend);

        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignore) {
            host = "";
        }
        getProxy().getPluginManager().registerListener(this, new Executor(host));
    }

    @Override
    public void onDisable() {
        shutdown = true;// To interrupt blocking.
        EntityQueue.QUEUE.offer(new Entity());
    }
}
