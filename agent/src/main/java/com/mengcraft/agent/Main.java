package com.mengcraft.agent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
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
public class Main extends JavaPlugin implements Listener, Agent {

    private final List<Node> handle = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        List<String> set = getConfig().getStringList("set");
        int size = set.size();
        for (int i = 0; i < size; i++) {
            handle.add(Node.decode(set.get(i)));
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, Message.CHANNEL);

        getServer().getServicesManager().register(Agent.class,
                this,
                this,
                ServicePriority.Normal);

        getServer().getPluginManager().registerEvents(this, this);
    }

    private final Queue<Message> queue = new LinkedList<>();

    @EventHandler
    public void handle(PlayerRegisterChannelEvent event) {
        if (eq(event.getChannel(), Message.CHANNEL)) {
            Player p = event.getPlayer();

            // Process queue at first
            while (!queue.isEmpty()) {
                p.sendPluginMessage(this, Message.CHANNEL, queue.poll().encode());
            }

            handle.forEach(node -> accept(p, node));
        }
    }

    private void accept(Player p, Node node) {
        if (node.accept(p)) {
            execute(node.getCommandList(p));
        }
    }

    @Override
    public void execute(Executor executor, List<String> command, boolean queued) {
        if (executor == Executor.BUNGEE) {
            Iterator it = getServer().getOnlinePlayers().iterator();
            if (it.hasNext()) {
                Player p = (Player) it.next();
                if (p.getListeningPluginChannels().contains(Message.CHANNEL)) {
                    p.sendPluginMessage(this, Message.CHANNEL, Message.encode(command));
                } else if (queued) {
                    queue.offer(Message.get(command));
                }
            } else if (queued) {
                queue.offer(Message.get(command));
            } else {
                getLogger().info("None player channel registered! Use queued.");
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void execute(Executor executor, List<String> command) {
        execute(executor, command, false);
    }

    @Override
    public void execute(List<String> command, boolean queued) {
        execute(Executor.BUNGEE, command, queued);
    }

    @Override
    public void execute(List<String> command) {
        execute(Executor.BUNGEE, command, false);
    }

    public static boolean eq(Object i, Object j) {
        return i == j || i.equals(j);
    }

}
