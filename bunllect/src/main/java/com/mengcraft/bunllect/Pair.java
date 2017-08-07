package com.mengcraft.bunllect;

import lombok.Data;

/**
 * Created by on 2017/8/8.
 */
@Data
public class Pair<L, R> {

    private final L left;
    private final R right;
}
