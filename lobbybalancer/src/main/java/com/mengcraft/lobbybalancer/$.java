package com.mengcraft.lobbybalancer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mengcraft.lobbybalancer.command.BalanceCommand;
import com.mengcraft.lobbybalancer.command.MainCommand;
import com.mengcraft.lobbybalancer.command.MappingCommand;
import com.mengcraft.lobbybalancer.listener.MainListener;
import com.mengcraft.lobbybalancer.listener.RTListListener;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Pattern;

/**
 * Created by on 10-11.
 */
public class $ extends Plugin {

    @Getter
    private static $ plugin;

    @Getter
    private static MainListener mainListener;

    @Getter
    private static boolean useUpdater;

    @Override
    @SneakyThrows
    public void onEnable() {
        plugin = this;
        val conf = new File(getDataFolder(), "config.json");
        if (!conf.exists()) {
            if (!getDataFolder().isDirectory() && !getDataFolder().mkdir()) {
                throw new IllegalStateException("data folder");
            }

            Files.copy(getResourceAsStream("config.json"), conf.toPath());
        }

        JsonElement element = new JsonParser().parse(new InputStreamReader(new FileInputStream(conf), StandardCharsets.UTF_8));
        for (JsonElement l : ((JsonObject) element).getAsJsonArray("pattern")) {
            ZoneMgr.INSTANCE.add(Pattern.compile(l.getAsString()));
        }

        getProxy().getScheduler().runAsync(this, () -> ZoneMgr.INSTANCE.updateAll(null));

        for (val mapping : ((JsonObject) element).getAsJsonObject("mapping").entrySet()) {
            getProxy().getPluginManager().registerCommand(this, new MappingCommand(mapping.getKey(), mapping.getValue().getAsJsonArray()));
        }

        useUpdater = ((JsonObject) element).get("multi_bungee").getAsBoolean();

        Plugin depend = getProxy().getPluginManager().getPlugin("bungeelistd");
        if (!$.nil(depend)) {
            getProxy().getPluginManager().registerListener(this, new RTListListener());
        }

        mainListener = new MainListener();
        getProxy().getPluginManager().registerListener(this, mainListener);

        getProxy().getPluginManager().registerCommand(this, new MainCommand());
        getProxy().getPluginManager().registerCommand(this, new BalanceCommand());
    }

    public static void log(Object message) {
        plugin.getLogger().info(String.valueOf(message));
    }

    public static boolean nil(Object any) {
        return any == null;
    }

    public static long now() {
        return System.currentTimeMillis();
    }

}
