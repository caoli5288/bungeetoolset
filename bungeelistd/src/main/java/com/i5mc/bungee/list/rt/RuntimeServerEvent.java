package com.i5mc.bungee.list.rt;

import lombok.Data;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Event;

@Data
public class RuntimeServerEvent extends Event {

    public enum Action {
        UP,
        DOWN;
    }

    private final ServerInfo serverInfo;
    private final Action action;
}
