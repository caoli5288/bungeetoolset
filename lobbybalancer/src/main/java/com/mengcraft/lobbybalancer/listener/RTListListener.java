package com.mengcraft.lobbybalancer.listener;

import com.i5mc.bungee.list.DynamicListEvent;
import com.i5mc.bungee.list.rt.RuntimeServerEvent;
import com.mengcraft.lobbybalancer.$;
import com.mengcraft.lobbybalancer.Info;
import com.mengcraft.lobbybalancer.InfoMgr;
import com.mengcraft.lobbybalancer.Zone;
import com.mengcraft.lobbybalancer.ZoneMgr;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RTListListener implements Listener {

    @EventHandler
    public void handle(DynamicListEvent event) {
        ZoneMgr.INSTANCE.updateAll(null);
    }

    @EventHandler
    public void handle(RuntimeServerEvent event) {
        ServerInfo info = event.getServerInfo();
        Zone zone = ZoneMgr.select(info);
        if (!$.nil(zone)) {
            RTAction.valueOf(event.getAction().name()).handle(zone, info);
        }
    }

    enum RTAction {

        UP {
            public void handle(Zone zone, ServerInfo info) {
                Info select = InfoMgr.select(info);
                select.update(zone);
            }
        },

        DOWN {
            public void handle(Zone zone, ServerInfo info) {
                Info i = InfoMgr.getByName(info.getName());
                if ($.nil(i)) {
                    return;
                }

                zone.remove(info.getName());
            }
        };

        public void handle(Zone zone, ServerInfo info) {
            throw new AbstractMethodError("handle");
        }
    }

}
