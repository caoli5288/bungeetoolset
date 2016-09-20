package com.mengcraft.agent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created on 16-9-8.
 */
public class Main extends JavaPlugin implements Listener, BungeeAgent {

    private final List<Node> nodeList = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        List<String> set = getConfig().getStringList("set");
        int size = set.size();
        for (int i = 0; i < size; i++) {
            nodeList.add(Node.decode(set.get(i)));
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, Message.CHANNEL);

        getServer().getServicesManager().register(BungeeAgent.class,
                this,
                this,
                ServicePriority.Normal);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        int size = nodeList.size();
        for (int i = 0; i < size; i++) {
            accept(p, nodeList.get(i));
        }
        if (!queue.isEmpty()) getServer().getScheduler().runTask(this, () -> processQueued());
    }

    private boolean processQueued() {
        if (!queue.isEmpty()) {
            Iterator it = getServer().getOnlinePlayers().iterator();
            if (!it.hasNext()) {
                return false;
            }

            Player p = (Player) it.next();
            for (List<String> list = queue.poll(); list != null; list = queue.poll()) {
                p.sendPluginMessage(this, Message.CHANNEL, Message.encode(list));
            }

            return true;
        }
        return false;
    }

    private void accept(Player p, Node node) {
        if (node.accept(p)) {
            execute(node.getCommandList(p));
        }
    }

    private final Queue<List> queue = new LinkedList<>();

    @Override
    public void execute(List<String> commandList) {
        execute(commandList, false);
    }

    @Override
    public void execute(List<String> commandList, boolean queued) {
        Iterator it = getServer().getOnlinePlayers().iterator();
        if (it.hasNext()) {
            Player p = (Player) it.next();
            p.sendPluginMessage(this, Message.CHANNEL, Message.encode(commandList));
        } else if (queued) {
            queue.offer(commandList);
        } else {
            throw new RuntimeException("None player channel registered! Use queued");
        }
    }

}
