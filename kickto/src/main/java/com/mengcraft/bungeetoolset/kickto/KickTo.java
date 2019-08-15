package com.mengcraft.bungeetoolset.kickto;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedList;

public class KickTo extends Plugin implements Listener {

    public static final String KEYWORD = "kick_to";
    public static final int LEN = KEYWORD.length();

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(ServerKickEvent event) {
        String reason = ChatColor.stripColor(event.getKickReason());
        System.out.println(reason);
        if (reason.startsWith(KEYWORD)) {
            ServerInfo info = getProxy().getServerInfo(reason.substring(LEN));
            if (info != null) {
                event.setCancelled(true);
                event.setCancelServer(info);
                LinkedList<String> joinQueue = new LinkedList<>();
                joinQueue.add(event.getKickedFrom().getName());
                ((UserConnection) event.getPlayer()).setServerJoinQueue(joinQueue);
            }
        }
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        if (event.getReason() == ServerConnectEvent.Reason.KICK_REDIRECT) {
            event.setCancelled(true);
            ((UserConnection) event.getPlayer()).connect(event.getTarget(), null, true, ServerConnectEvent.Reason.LOBBY_FALLBACK);
        }
    }
}
