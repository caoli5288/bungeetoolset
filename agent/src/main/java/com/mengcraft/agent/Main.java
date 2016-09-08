package com.mengcraft.agent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    }

    @Override
    public void execute(List<String> commandList) {
        Iterator it = getServer().getOnlinePlayers().iterator();
        if (!it.hasNext()) {
            throw new RuntimeException();
        }
        Player p = (Player) it.next();
        p.sendPluginMessage(this, Message.CHANNEL, new Message(commandList).encode());
    }

    private void accept(Player p, Node node) {
        if (node.accept(p)) {
            execute(node.getCommandList(p));
        }
    }

}
