package com.mengcraft.lobbybalancer.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mengcraft.lobbybalancer.$;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Iterator;

public class MappingCommand extends Command {

    private String head, label;

    public MappingCommand(String name, JsonArray mapping) {
        super(name);
        Iterator<JsonElement> iterator = mapping.iterator();
        head = label = iterator.next().getAsString();
        if (iterator.hasNext()) {
            StringBuilder b = new StringBuilder();
            while (iterator.hasNext()) {
                b.append(iterator.next().getAsString());
            }
            label += b;
        }
    }

    @Override
    public void execute(CommandSender p, String[] input) {
        if (!(p instanceof UserConnection)) {
            return;
        }

        if (input.length == 1 && !input[0].isEmpty()) {
            $.getMainListener().joinForce(((UserConnection) p), head + input[0]);
            return;
        }

        BalanceCommand.process((UserConnection) p, label);
    }

}
