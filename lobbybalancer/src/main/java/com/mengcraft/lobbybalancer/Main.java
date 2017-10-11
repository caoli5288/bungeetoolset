package com.mengcraft.lobbybalancer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by on 10-11.
 */
public class Main extends Plugin {

    static Main inst;

    @Override
    @SneakyThrows
    public void onEnable() {
        inst = this;
        val conf = new File(getDataFolder(), "config.json");
        if (!conf.exists()) {
            Files.copy(getResourceAsStream("config.json"), conf.toPath());
        }

        JsonElement element = new JsonParser().parse(new InputStreamReader(new FileInputStream(conf), "UTF-8"));
        for (JsonElement l : ((JsonObject) element).getAsJsonArray("pattern")) {
            ZoneMgr.INST.add(Pattern.compile(l.getAsString()));
        }

        getProxy().getScheduler().schedule(this, ZoneMgr.INST::updateAll, 30, TimeUnit.SECONDS);
    }

    public static void log(Object message) {
        inst.getLogger().info(String.valueOf(message));
    }

}