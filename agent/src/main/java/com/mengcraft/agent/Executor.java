package com.mengcraft.agent;

/**
 * Created on 16-9-28.
 */
public enum Executor {

    BUNGEE,
    BUKKIT;

    public static Executor get(int index) {
        return values()[index];
    }

}
