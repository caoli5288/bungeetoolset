package com.i5mc.bungee.list;

import com.i5mc.bungee.list.rt.RT;
import com.i5mc.bungee.list.rt.RTInfoMgr;
import com.i5mc.bungee.list.rt.RTServer;
import lombok.SneakyThrows;
import lombok.val;
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
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 16-8-25.
 */
public class Main extends Plugin implements Listener {

    public static final File FOLDER = new File("list.d");

    private Map<String, ServerInfo> active;
    private boolean waterfall;
    static File i;

    @SneakyThrows
    public void onEnable() {
        waterfall = getProxy().getName().equals("Waterfall");

        if (!FOLDER.isDirectory()) {
            val prev = new File("server.list.d");
            if (prev.isDirectory()) {
                if (!prev.renameTo(FOLDER) && !FOLDER.mkdir()) {
                    throw new RuntimeException(FOLDER + " not a directory!");
                }
            } else {
                if (!FOLDER.mkdir()) {
                    throw new RuntimeException(FOLDER + " not a directory!");
                }
            }
        }

        process(getProxy().getServers());// init
        InjectedAdapter.inject(this);

        if (!waterfall) getProxy().getPluginManager().registerListener(this, this);

        if (!getDataFolder().isDirectory() && !getDataFolder().mkdir()) {
            throw new IllegalStateException("data folder");
        }

        i = new File(getDataFolder(), "config.yml");
        if (!i.isFile()) {
            Files.copy(getResourceAsStream("config.yml"), i.toPath());
        }

        RT.load(i);
        if (RT.INSTANCE.isListen()) {
            RTServer.init(getExecutorService(),
                    getLogger(),
                    getProxy().getScheduler().schedule(this, RTInfoMgr::valid, 5, 15, TimeUnit.SECONDS)
            );
            getLogger().info("RT server listen on :" + RT.PORT);
        }

        getProxy().getPluginManager().registerCommand(this, new CommandExec());
    }

    @Override
    public void onDisable() {
        if (!RTServer.isClosed()) {
            getLogger().info("RT server shutdown");
            RTServer.close();
        }
    }

    @SneakyThrows
    public void process(Map<String, ServerInfo> input) {
        Files.walk(FOLDER.toPath(), 1).forEach(path -> {
            File f = path.toFile();
            if (FileProcessor.INSTANCE.accept(f)) {
                FileProcessor.INSTANCE.process(input, f);
            } else if (RemoteProcessor.INSTANCE.accept(f)) {
                RemoteProcessor.INSTANCE.process(input, f);
            }
        });
        if (!RTServer.isClosed()) {// RT update
            for (val i : RTInfoMgr.alive()) {
                input.put(i.getName(), i);
            }
        }
        getLogger().info("Invoke dynamic server list done!");
    }

    public void setActive(Map<String, ServerInfo> active) {
        this.active = active;
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

}
