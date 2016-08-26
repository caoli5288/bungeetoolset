package com.mengcraft.serverlist;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created on 16-8-26.
 */
public abstract class Processor {

    protected void put(Map<String, ServerInfo> map, BungeeServerInfo info) {
        map.put(info.getName(), info);
    }

    protected InetSocketAddress toAddress(String host, String port) {
        return toAddress(host, Integer.parseInt(port));
    }

    protected InetSocketAddress toAddress(String host, int port) {
        return new InetSocketAddress(host, port);
    }

    public abstract boolean accept(File f);

    public abstract void process(Map<String, ServerInfo> map, File f);

}
