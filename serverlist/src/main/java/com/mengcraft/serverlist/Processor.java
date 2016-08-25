package com.mengcraft.serverlist;

import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.util.Map;

/**
 * Created on 16-8-26.
 */
public interface Processor {
    void process(Map<String, ServerInfo> map, File f);
}
