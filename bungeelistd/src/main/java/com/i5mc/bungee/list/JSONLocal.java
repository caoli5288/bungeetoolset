package com.i5mc.bungee.list;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by on 2017/7/25.
 */
public class JSONLocal {

    private static final ThreadLocal<Gson> LOCAL = new SuppliedLocal<>(() -> new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create());

    private static Gson get() {
        return LOCAL.get();
    }

    public static <T> T load(Reader r, Class<T> type) {
        return get().fromJson(r, type);
    }

    static final class SuppliedLocal<T> extends ThreadLocal<T> {

        private final Supplier<? extends T> supplier;

        SuppliedLocal(Supplier<? extends T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T initialValue() {
            return supplier.get();
        }
    }

}
