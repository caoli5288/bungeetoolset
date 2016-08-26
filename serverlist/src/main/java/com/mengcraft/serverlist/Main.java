package com.mengcraft.serverlist;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.conf.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Map;

/**
 * Created on 16-8-25.
 */
public class Main extends Plugin {

    @Override
    public void onEnable() {
        if (!folder.isDirectory()) {
            if (folder.mkdir()) {
                throw new RuntimeException(folder + " not a directory!");
            }
        }

        try {
            Field field = Configuration.class.getDeclaredField("servers");
            field.setAccessible(true);
            Map map = Map.class.cast(field.get(getProxy().getConfig()));
            process(map);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        getProxy().setConfigurationAdapter(new InjectedAdapter(this));
    }

    public void process(Map<String, ServerInfo> map) {
        try {
            Files.walk(folder.toPath(), 1).forEach(path -> {
                File f = path.toFile();
                if (FileProcessor.INSTANCE.accept(f)) {
                    FileProcessor.INSTANCE.process(map, f);
                } else if (RemoteProcessor.INSTANCE.accept(f)) {
                    RemoteProcessor.INSTANCE.process(map, f);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final File folder = new File("server.list.d");

}
