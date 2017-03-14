package com.i5mc.bungee.channel;

import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import static com.i5mc.bungee.channel.ChannelMessage.CHANNEL;

/**
 * Created on 17-3-15.
 */
public class I5Channel extends JavaPlugin implements Listener {

    static final Map<String, Consumer<ChannelMessage>> HANDLE = new HashMap<>();
    static final Queue<byte[]> QUEUE = new LinkedList<>();
    static I5Channel plugin;

    public void handle(Listener l, Event i) {
        if (QUEUE.isEmpty()) return;
        val e = ((PlayerRegisterChannelEvent) i);
        if (!e.getChannel().equals(CHANNEL)) return;
        getServer().getScheduler().runTask(this, () -> {
            while (!QUEUE.isEmpty()) {
                e.getPlayer().sendPluginMessage(this, CHANNEL, QUEUE.poll());
            }
        });
    }

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, (l, r, buf) -> {
            try {
                val message = ChannelMessage.decode(buf);
                val con = HANDLE.get(message.getLabel());
                if (!$.nil(con)) con.accept(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        val l = new RegisteredListener(this, this::handle, EventPriority.NORMAL, this, false);
        PlayerRegisterChannelEvent.getHandlerList().register(l);
    }

    public Player pick() {
        val list = getServer().getOnlinePlayers().iterator();
        for (Player p; list.hasNext(); ) {
            p = list.next();
            if (p.getListeningPluginChannels().contains(CHANNEL)) return p;
        }
        return null;
    }

    public static void send(ChannelMessage message) {
        byte[] buf = message.encode();// fast fail here
        val p = plugin.pick();
        if (!$.nil(p)) {
            p.sendPluginMessage(plugin, CHANNEL, buf);
        } else if (message.isQueued()) {
            QUEUE.add(buf);
        }
    }

    public static void listen(String label, Consumer<ChannelMessage> consumer) {
        $.valid(HANDLE.get(label) == null, "Already");
        HANDLE.put(label, consumer);
    }

}
