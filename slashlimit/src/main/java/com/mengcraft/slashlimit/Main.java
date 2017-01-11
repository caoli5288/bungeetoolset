package com.mengcraft.slashlimit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Created on 16-2-19.
 */
public class Main extends Plugin {

    public static final boolean DEBUG = true;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.json");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.json")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                throw new StartupException("Cannot save configure!");
            }
        }

        try {
            FileReader reader = new FileReader(file);
            JsonObject parsed = new JsonParser().parse(reader).getAsJsonObject();

            long time = parsed.get("time").getAsLong();
            int count = parsed.get("count").getAsInt();
            int cache = parsed.get("cache").getAsInt();

            Limiter<UUID> limiter = new Limiter<>(time * 1000, count, cache);
            EventExecutor executor = new EventExecutor(this, limiter);

            executor.setMessage(parsed.get("message").getAsString());
            if (DEBUG) {
                getLogger().info("Startup done! Limit switch " + count +
                        " in " + time + " seconds!");
            }
            getProxy().getPluginManager().registerListener(this, executor);
        } catch (FileNotFoundException e) {
            throw new StartupException("Configure parse error!");
        }

    }

}
