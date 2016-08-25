package com.mengcraft.serverlist;

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
        if (!DIR.isDirectory()) {
            if (DIR.mkdir()) {
                throw new RuntimeException(DIR + " not a directory!");
            }
        }
        try {
            Field field = Configuration.class.getDeclaredField("servers");
            field.setAccessible(true);
            Map map = Map.class.cast(field.get(getProxy().getConfig()));
            ListProcessor.process(this, map);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        getProxy().setConfigurationAdapter(new InjectedAdapter(this));
    }

    public static final File DIR = new File("server.list.d");

}
