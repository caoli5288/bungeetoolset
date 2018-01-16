package com.mengcraft.bunllect;

import lombok.Data;

import java.time.LocalDate;

/**
 * Created by on 1月1日.
 */
@Data
public class DateValidObject<T> {

    private final T object;
    private final LocalDate date = LocalDate.now();

    public T get() {
        if (date.isEqual(LocalDate.now())) {
            return object;
        }
        return null;
    }
}
