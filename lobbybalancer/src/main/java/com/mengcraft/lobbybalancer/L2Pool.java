package com.mengcraft.lobbybalancer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import lombok.val;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public enum L2Pool {

    INSTANCE;

    private final Cache<String, Object> pool = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    private final Object invalid = new Object();

    @SneakyThrows
    public static <T> T load(String key, Supplier<T> supplier) {
        val out = INSTANCE.pool.get(key, () -> {
            val l = supplier.get();
            return l == null ? INSTANCE.invalid : l;
        });
        return out == INSTANCE.invalid ? null : (T) out;
    }

    public static Map<String, Object> map() {
        return INSTANCE.pool.asMap();
    }

}
