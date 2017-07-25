package com.i5mc.bungee.list;

import com.google.gson.Gson;

import java.io.Reader;

/**
 * Created by on 2017/7/25.
 */
public class JSONLocal {

    private static final ThreadLocal<Gson> LOCAL = ThreadLocal.withInitial(Gson::new);

    private static Gson get() {
        return LOCAL.get();
    }

    public static <T> T parse(Reader r, Class<T> type) {
        return get().fromJson(r, type);
    }
}
