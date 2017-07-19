package com.i5mc.bungee.list.rt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * Created on 17-7-19.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "handle")
public class Info {

    private final ServerInfo handle;
    private int alive;

    public boolean valid() {
        return !((alive == -1) || (--alive == -1));
    }

}
