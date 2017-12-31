package com.mengcraft.bunllect;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import lombok.val;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Created by on 1月1日.
 */
public enum L2Pool {

    INSTANCE;

    private final Cache<String, Object> pool = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build();

    private final Object invalid = new Object();

    @SneakyThrows
    public static <T> T pull(String key, Supplier<T> supplier) {
        val pulled = INSTANCE.pool.get(key, () -> {
            T value = supplier.get();
            return value == null ? INSTANCE.invalid : value;
        });
        return pulled == INSTANCE.invalid ? null : (T) pulled;
    }

    public static void expire(String key) {
        INSTANCE.pool.invalidate(key);
    }

    public static void expire(Pattern express) {
        INSTANCE.pool.asMap().keySet().removeIf(key -> express.matcher(key).matches());
    }

}
