package com.i5mc.bungee.list.rt;

import com.i5mc.bungee.list.JSONLocal;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created on 17-7-19.
 */
@Data
public class RT {

    public static final int PORT = 22275;

    private boolean listen;
    private boolean log;

    private String group;
    private List<String> dist;

    @SneakyThrows
    public static void load(File l) {
        INSTANCE.load(JSONLocal.parse(new FileReader(l), RT.class));
    }

    void load(RT l) {
        listen = l.listen;
        dist = l.dist;
        log = l.log;
        group = l.group;
    }

    public static final RT INSTANCE = new RT();
}
