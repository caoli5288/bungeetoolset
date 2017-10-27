package com.mengcraft.lobbybalancer;

import com.i5mc.bungee.list.DynamicListEvent;
import com.i5mc.bungee.list.rt.RuntimeServerEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DynListListener implements Listener {

    @EventHandler
    public void handle(DynamicListEvent event) {
        ZoneMgr.INST.updateAll(null);
    }

    @EventHandler
    public void handle(RuntimeServerEvent event) {
        if (event.getAction() == RuntimeServerEvent.Action.UP) {
            ServerInfo info = event.getServerInfo();
            Zone zone = ZoneMgr.select(info);
            if (!(zone == Zone.NIL)) {
                if (!InfoMgr.check(info)) {
                    InfoMgr.mapping(info).update(zone);
                }
            }
        }
    }
}
