package com.i5mc.bungee.list.rt;

import com.i5mc.bungee.list.rt.protocol.Heartbeat;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Created on 17-7-22.
 */
public class RTClient extends JavaPlugin {

    private LinkedList<String> l;

    @SneakyThrows
    void reload() {
        RT.load(new File(getDataFolder(), "config.yml"));
        l = new LinkedList<>(RT.INSTANCE.getDist());
    }

    void sendPacket() {
        val endpoint = l.element();
        runAsync(() -> {
            try (val cli = new Socket()) {
                cli.setSoTimeout(4000);
                cli.connect(new InetSocketAddress(endpoint, RT.PORT));
                val p = new Heartbeat(RT.INSTANCE.getGroup(), cli.getLocalAddress().getHostAddress(), Bukkit.getPort());
                Protocol.send(cli, p);
            } catch (Exception ign) {
                if (RT.INSTANCE.isDebug()) {
                    getLogger().warning("RT server " + endpoint + " refused");
                }
                l.poll();
                if (!l.isEmpty()) keepAlive();
            }
        });
    }

    void keepAlive() {
        if (l.isEmpty()) {
            reload();
        }
        sendPacket();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload();

        val group = RT.INSTANCE.getGroup();
        if (!(group == null || group.isEmpty())) {
            Bukkit.getScheduler().runTaskTimer(this, this::keepAlive, 50, 200);
        }
    }

}
