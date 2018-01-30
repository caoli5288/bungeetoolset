package com.mengcraft.lobbybalancer.command;

import com.mengcraft.lobbybalancer.$;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MappingCommand extends Command {

    private final String mapping;

    public MappingCommand(String name, String mapping) {
        super(name);
        this.mapping = mapping;
    }

    @Override
    public void execute(CommandSender p, String[] input) {
        if (!(p instanceof UserConnection)) {
            return;
        }

        if (input.length == 1 && !input[0].isEmpty()) {
            $.getMainListener().joinForce(((UserConnection) p), getName() + "-" + input[0]);
            return;
        }

        BalanceCommand.process((UserConnection) p, mapping);
    }

}
