package com.i5mc.bungee.list;

import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.conf.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.util.CaseInsensitiveMap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created on 16-8-25.
 */
public class Main extends Plugin implements Listener {

    private Map<String, ServerInfo> active;
    private boolean waterfall;

    @Override
    public void onEnable() {
        waterfall = getProxy().getName().equals("Waterfall");

        if (!folder.isDirectory()) {
            if (!folder.mkdir()) {
                throw new RuntimeException(folder + " not a directory!");
            }
        }

        process(getProxy().getServers());// init

        getProxy().setConfigurationAdapter(new InjectedAdapter(this));
        if (!waterfall) getProxy().getPluginManager().registerListener(this, this);
    }

    public void process(Map<String, ServerInfo> in) {
        try {
            Files.walk(folder.toPath(), 1).forEach(path -> {
                File f = path.toFile();
                if (FileProcessor.INSTANCE.accept(f)) {
                    FileProcessor.INSTANCE.process(in, f);
                } else if (RemoteProcessor.INSTANCE.accept(f)) {
                    RemoteProcessor.INSTANCE.process(in, f);
                }
            });
            getLogger().info("Invoke dynamic server list done!");
        } catch (IOException ignore) {
        }
    }

    public Map<String, ServerInfo> newActive(Map<String, ServerInfo> in) {
        return active = new HashMap<>(in);
    }

    private Field field;

    public Field getField() {
        if (field == null) {
            try {
                field = Configuration.class.getDeclaredField("servers");
                field.setAccessible(true);
            } catch (NoSuchFieldException ignore) {
            }
        }
        return field;
    }

    @EventHandler
    public void handle(ProxyReloadEvent event) {
        try {// add remove and modify support for bungee
            process(active);
            getProxy().getServers().forEach((name, info) -> {
                if (!active.containsKey(name) || !eq(active.get(name), info)) {
                    removeInfo(info);
                } else {
                    active.put(name, info);// override info
                }
            });
            getField().set(getProxy().getConfig(), new CaseInsensitiveMap<>(active));
        } catch (IllegalAccessException ignore) {
        }
    }

    private boolean eq(ServerInfo i, ServerInfo j) {
        return i.getAddress().equals(j.getAddress());
    }

    private void removeInfo(ServerInfo info) {
        info.getPlayers().forEach(p -> removeInfo(p));
    }

    private void removeInfo(ProxiedPlayer p) {
        ServerInfo target = pick(p.getPendingConnection().getListener());
        if (target == null) {
            p.disconnect(getProxy().getTranslation("fallback_kick", "fallback not found on reload"));
        } else {
            p.connect(target, (success, cause) -> {
                if (!success) {
                    p.disconnect(getProxy().getTranslation("fallback_kick", cause.getCause().getClass().getName()));
                }
            });
        }
    }

    private ServerInfo pick(ListenerInfo in) {
        Iterator<String> it = in.getServerPriority().iterator();
        ServerInfo out = null;
        while (out == null && it.hasNext()) {
            String next = it.next();
            if (active.containsKey(next)) {
                out = active.get(next);
            }
        }
        return out;
    }

    public boolean isWaterfall() {
        return waterfall;
    }

    public final File folder = new File("server.list.d");

}
