package com.i5mc.bungee.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public class $ {
    public static <T> String join(Collection<T> input, Function<T, String> func, String separator) {
        StringBuilder b = new StringBuilder();
        Iterator<T> itr = input.iterator();
        while (itr.hasNext()) {
            b.append(func.apply(itr.next()));
            if (itr.hasNext()) {
                b.append(separator);
            }
        }
        return b.toString();
    }
}
