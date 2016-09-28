package com.mengcraft.agent;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
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
        getServer().getMessenger().registerIncomingPluginChannel(this, Message.CHANNEL, new MessageListener(this));

        getServer().getServicesManager().register(Agent.class,
                this,
                this,
                ServicePriority.Normal);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command i, String label, String[] j) {
        Iterator<String> it = Arrays.asList(j).iterator();
        if (it.hasNext()) {
            String next = it.next();
            if (eq(next, "bungee") && it.hasNext()) {
                return sendBungee(sender, it);
            } else {
                sender.sendMessage("/agent bungee [command...]");
            }
        } else {
            sender.sendMessage("/agent bungee [command...]");
        }
        return false;
    }

    private boolean sendBungee(CommandSender sender, Iterator<String> it) {
        StringBuilder builder = new StringBuilder(it.next());
        while (it.hasNext()) {
            builder.append(" ");
            builder.append(it.next());
        }
        return sendBungee(sender, builder.toString());
    }

    private boolean sendBungee(CommandSender sender, String command) {
        String info = ChatColor.GREEN + "Send " + command + " done";
        getLogger().info(info);
        if (sender instanceof Player) {
            sender.sendMessage(info);
        }
        execute(Arrays.asList(command));
        return true;
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
        Message message = Message.get(executor, command, queued);
        Player player = getPlayer();
        if (eq(player, null)) {
            if (queued) {
                queue.offer(message);
            } else {
                getLogger().warning("None player channel registered!");
            }
        } else {
            player.sendPluginMessage(this, Message.CHANNEL, message.encode());
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

    private Player getPlayer() {
        Iterator<? extends Player> it = getServer().getOnlinePlayers().iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p.getListeningPluginChannels().contains(Message.CHANNEL)) {
                return p;
            }
        }
        return null;
    }

    public static boolean eq(Object i, Object j) {
        return i == j || i.equals(j);
    }

}
