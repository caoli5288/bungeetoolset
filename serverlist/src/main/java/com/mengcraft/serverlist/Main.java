package com.mengcraft.serverlist;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.conf.Configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created on 16-8-25.
 */
public class Main extends Plugin {

    @Override
    public void onEnable() {
        if (!dir.isDirectory()) {
            if (dir.mkdir()) {
                throw new RuntimeException(dir + " not a directory!");
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
        String[] list = dir.list();
        if (list != null) {
            for (String name : list) {
                process(map, name);
            }
        }
    }

    private void process(Map<String, ServerInfo> map, String name) {
        if (name.endsWith(".list")) {
            FileProcessor.INSTANCE.process(map, new File(dir, name));
        } else if (name.endsWith(".remote")) {
            RemoteProcessor.INSTANCE.process(map, new File(dir, name));
        }
    }

    public final File dir = new File("server.list.d");

}
