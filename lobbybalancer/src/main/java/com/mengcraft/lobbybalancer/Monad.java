package com.mengcraft.lobbybalancer;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class Monad<T> {

    private final T obj;

    public static <T> Monad<T> obj(T obj) {
        return new Monad<>(obj);
    }

    public <R> Monad<R> get(Function<T, R> functor) {
        return obj(getObj(functor));
    }

    public <R> R getObj(Function<T, R> functor) {
        if (obj != null) {
            return functor.apply(obj);
        }
        return null;
    }

    public T getObj() {
        return obj;
    }

    public boolean isEmpty() {
        return obj == null;
    }
}
