package com.i5mc.bungee.channel;

/**
 * Created on 17-3-15.
 */
public class $ {

    public static void valid(boolean b, String message) {
        if (b) throw new IllegalStateException(message);
    }

    public static boolean nil(Object any) {
        return any == null;
    }

    public static int now() {
        return Long.valueOf(System.currentTimeMillis() / 1000).intValue();
    }
}
