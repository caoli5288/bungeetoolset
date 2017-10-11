package com.mengcraft.lobbybalancer;

import lombok.val;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.function.Consumer;

/**
 * Created by on 10-11.
 */
public class MainCommand extends Command {

    public MainCommand() {
        super("lobbybalancer", "lobbybalancer.admin");
    }

    enum Sub {

        HEAD(ZoneMgr.INST::sendHead),
        F5(ZoneMgr.INST::updateAll),
        ZONE_REF(ZoneMgr.INST::sendAll);

        final Consumer<CommandSender> func;

        Sub(Consumer<CommandSender> func) {
            this.func = func;
        }

        public void apply(CommandSender who) {
            func.accept(who);
        }
    }

    @Override
    public void execute(CommandSender who, String[] input) {
        if (input.length == 1) {
            try {
                Sub.valueOf(input[0].replace('-', '_').toUpperCase()).apply(who);
            } catch (IllegalArgumentException ign) {
                ;
            }
        } else {
            for (val sub : Sub.values()) {
                who.sendMessage("/lobbybalancer " + sub.name().toLowerCase());
            }
        }
    }
}
