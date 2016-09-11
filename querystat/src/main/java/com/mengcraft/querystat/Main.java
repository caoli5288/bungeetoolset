package com.mengcraft.querystat;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created on 16-9-10.
 */
public class Main extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new CommandExecutor(getProxy()));
    }

}
