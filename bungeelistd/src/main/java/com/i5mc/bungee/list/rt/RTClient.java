package com.i5mc.bungee.list.rt;

import com.google.common.collect.ImmutableList;
import com.i5mc.bungee.list.rt.protocol.Dist;
import com.i5mc.bungee.list.rt.protocol.Heartbeat;
import com.i5mc.bungee.list.rt.protocol.Pull;
import com.i5mc.bungee.list.rt.protocol.PullReq;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Created on 17-7-22.
 */
public class RTClient extends JavaPlugin {

    private LinkedList<String> l;
    private RTDiscover discover;

    @SneakyThrows
    void reload() {
        RT.load(new File(getDataFolder(), "config.yml"));
        l = new LinkedList<>(RT.INSTANCE.getDist());
    }

    public List<PullReq.Req> pull(String group) {
        val endpoint = l.element();
        try (val cli = conn(endpoint)) {
            val p = new Pull(group);
            Protocol.output(cli.getOutputStream(), p);
            val receive = Protocol.input(cli.getInputStream());
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
        cli.connect(new InetSocketAddress(endpoint, RT.PORT), 4000);
        return cli;
    }

    @SneakyThrows
    void sendAlive() {
        if (discover == null) {
            val endpoint = l.element();
            runAsync(() -> {
                try (val cli = conn(endpoint)) {
                    val p = new Heartbeat(RT.INSTANCE.getGroup(), cli.getLocalAddress().getHostAddress(), Bukkit.getPort(), RT.INSTANCE.isFixedId());
                    Protocol.output(cli.getOutputStream(), p);
                    log(p);
                } catch (Exception ign) {
                    if (RT.INSTANCE.isLog()) {
                        getLogger().warning("RT server " + endpoint + " refused");
                    }
                    l.poll();
                    if (!l.isEmpty()) keepAlive();
                }
            });
        } else {
            byte[] ch = RTDiscover.PUB.getBytes("utf-8");
            runAsync(() -> {
                try (val cli = discover.getPool().getResource()) {
                    val p = new Dist(RT.INSTANCE.getGroup(), discover.getLocalhost(), Bukkit.getPort(), RT.INSTANCE.isFixedId());
                    val buf = new ByteArrayOutputStream();
                    Protocol.output(buf, p);
                    cli.publish(ch, buf.toByteArray());
                    log(p);
                } catch (Exception e) {
                    log(e);
                }
            });
        }
    }

    public void log(Object any) {
        if (RT.INSTANCE.isLog()) {
            getLogger().log(Level.INFO, "" + any);
        }
    }

    void keepAlive() {
        if (discover == null && l.isEmpty()) reload();
        sendAlive();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reload();

        val group = RT.INSTANCE.getGroup();
        if (!(group == null || group.isEmpty())) {
            val cover = RT.INSTANCE.getDiscover();
            if (!(cover == null)) {
                val discover = new RTDiscover(cover);
                try {
                    if (discover.init()) {
                        RTClient.this.discover = discover;
                        getLogger().log(Level.INFO, "Discover service okay");
                    }
                } catch (IOException e) {
                    getLogger().log(Level.INFO, "Discover service failed. " + e.getMessage());
                }
            }
            Bukkit.getScheduler().runTaskTimer(this, this::keepAlive, 50, 200);
        }
    }

}
