package com.mengcraft.serverlist;

import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collection;
import java.util.Map;

/**
 * Created on 16-8-25.
 */
public class InjectedAdapter implements ConfigurationAdapter {

    private final ConfigurationAdapter proto;
    private final Main main;

    public InjectedAdapter(Main main) {
        this.main = main;
        proto = main.getProxy().getConfigurationAdapter();
    }

    @Override
    public void load() {
        proto.load();
    }

    @Override
    public int getInt(String s, int i) {
        return proto.getInt(s, i);
    }

    @Override
    public String getString(String s, String s1) {
        return proto.getString(s, s1);
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return proto.getBoolean(s, b);
    }

    @Override
    public Collection<?> getList(String s, Collection<?> collection) {
        return proto.getList(s, collection);
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        Map<String, ServerInfo> out = proto.getServers();
        ListProcessor.process(main, out);
        return out;
    }

    @Override
    public Collection<ListenerInfo> getListeners() {
        return proto.getListeners();
    }

    @Override
    public Collection<String> getGroups(String s) {
        return proto.getGroups(s);
    }

    @Override
    public Collection<String> getPermissions(String s) {
        return proto.getPermissions(s);
    }

}
