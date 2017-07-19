package com.i5mc.bungee.list.rt;

import lombok.Data;

import java.util.List;

/**
 * Created on 17-7-19.
 */
@Data
public class RT {

    private boolean listen;
    private List dist;

    public static final int PORT = 22275;
}
