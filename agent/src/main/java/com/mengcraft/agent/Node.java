package com.mengcraft.agent;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16-9-8.
 */
public class Node {

    private final String permission;
    private final List<String> commandList;

    private Node(String permission, List<String> commandList) {
        this.permission = permission;
        this.commandList = commandList;
    }

    public boolean accept(Player p) {
        return p.hasPermission(permission);
    }

    public List<String> getCommandList(Player p) {
        int size = commandList.size();
        List<String> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            out.add(commandList.get(i).replace("%player%", p.getName()));
        }
        return out;
    }

    public static Node decode(String in) {
        int i = in.indexOf('|');
        if (i < 0) {
            throw new IllegalArgumentException(in);
        }
        return new Node(in.substring(0, i), Lists.newArrayList(in.substring(i + 1).split(";")));
    }

}
