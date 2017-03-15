package com.i5mc.bungee.list;

import lombok.val;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 16-8-25.
 */
public class InjectedAdapter implements ConfigurationAdapter {

    private final ConfigurationAdapter proto;
    private final Main main;

    InjectedAdapter(Main main, ConfigurationAdapter proto) {
        this.main = main;
        this.proto = proto;
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
        if (main.isWaterfall()) {
            main.process(out);
        } else {
            main.setActive(new HashMap<>(out));
            main.getProxy().getServers().forEach((name, info) -> {
                if (!out.containsKey(name)) {
                    out.put(name, info);
                }// fix exception on origin proxy
            });
        }
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

    public static void inject(Main main) {
        val p = main.getProxy().getConfigurationAdapter();
        if (p instanceof InjectedAdapter) throw new IllegalStateException();
        val i = new InjectedAdapter(main, p);
        main.getProxy().setConfigurationAdapter(i);
    }

}
