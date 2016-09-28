package com.mengcraft.agent;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 16-9-8.
 */
public class Node {

    private final String permission;
    private final List<String> command;

    private Node(String permission, List<String> command) {
        this.permission = permission;
        this.command = command;
    }

    public boolean accept(Player p) {
        return p.hasPermission(permission);
    }

    public List<String> getCommandList(Player p) {
        int size = command.size();
        List<String> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            out.add(command.get(i).replace("%player%", p.getName()));
        }
        return out;
    }

    public static Node decode(String in) {
        int i = in.indexOf('|');
        if (i < 0 || i + 1 == in.length()) {
            throw new IllegalArgumentException(in);
        }
        return new Node(in.substring(0, i), Arrays.asList(in.substring(i + 1).split(";")));
    }

}
