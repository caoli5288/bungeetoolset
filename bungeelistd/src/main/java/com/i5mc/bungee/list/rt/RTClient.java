package com.i5mc.bungee.list.rt;

import com.google.common.collect.ImmutableList;
import com.i5mc.bungee.list.rt.protocol.Heartbeat;
import com.i5mc.bungee.list.rt.protocol.Pull;
import com.i5mc.bungee.list.rt.protocol.PullReq;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

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

    public List<PullReq.Req> pull(String group) {
        val endpoint = l.element();
        try (val cli = conn(endpoint)) {
            val p = new Pull(group);
            Protocol.send(cli, p);
            val receive = Protocol.input(cli);
            return ((PullReq) receive).getAlive();
        } catch (Exception ign) {
            l.poll();
            if (!l.isEmpty()) return pull(group);
        }
        return ImmutableList.of();
    }

    @SneakyThrows
    Socket conn(String endpoint) {
        val cli = new Socket();
        cli.setSoTimeout(4000);
        cli.connect(new InetSocketAddress(endpoint, RT.PORT));
        return cli;
    }

    void sendAlive() {
        val endpoint = l.element();
        runAsync(() -> {
            try (val cli = conn(endpoint)) {
                val p = new Heartbeat(RT.INSTANCE.getGroup(), cli.getLocalAddress().getHostAddress(), Bukkit.getPort());
                Protocol.send(cli, p);
            } catch (Exception ign) {
                if (RT.INSTANCE.isLog()) {
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
        sendAlive();
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
