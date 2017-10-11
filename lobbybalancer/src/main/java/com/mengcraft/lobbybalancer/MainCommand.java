package com.mengcraft.lobbybalancer;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by on 10-11.
 */
public class MainCommand extends Command {

    public MainCommand() {
        super("lobbybalancer", "lobbybalancer.admin");
    }

    @Override
    public void execute(CommandSender who, String[] input) {
        ZoneMgr.INST.sendAll(who);
    }
}
