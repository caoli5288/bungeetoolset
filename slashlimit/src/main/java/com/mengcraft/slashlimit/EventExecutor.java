package com.mengcraft.slashlimit;

import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

import static com.mengcraft.slashlimit.Main.DEBUG;

/**
 * Created on 16-2-19.
 */
public class EventExecutor implements Listener {

    private final Limiter<UUID> limiter;
    private final Main main;
    private String message;

    public EventExecutor(Main main, Limiter<UUID> limiter) {
        this.main = main;
        this.limiter = limiter;
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        if (!limiter.valid(event.getPlayer().getUniqueId())) {
            if (DEBUG) {
                main.getLogger().info("Kick player " + event.getPlayer() + " due to fast switch!");
            }
            event.getPlayer().disconnect(message);
        }
    }


    public void setMessage(String message) {
        this.message = message;
    }

}
