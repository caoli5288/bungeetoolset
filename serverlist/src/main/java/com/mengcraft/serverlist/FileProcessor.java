package com.mengcraft.serverlist;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created on 16-8-25.
 */
public class FileProcessor implements Processor {

    public static final Processor INSTANCE = new FileProcessor();

    private FileProcessor() {
    }

    private boolean process(Map<String, ServerInfo> map, File file, String line) {
        StringTokenizer it = new StringTokenizer(line);
        try {
            String token = it.nextToken();
            if (token.charAt(0) == '#') {
                return false;// ignore this line
            }
            if (token.equals("server")) {
                add(new BungeeServerInfo(
                        it.nextToken(),
                        new InetSocketAddress(it.nextToken(), Integer.parseInt(it.nextToken())),
                        "",
                        it.hasMoreTokens() && it.nextToken().equals("restricted")
                ), map);
                return true;
            }
            throw new RuntimeException("unknown token " + token + " in " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void add(BungeeServerInfo info, Map<String, ServerInfo> map) {
        map.put(info.getName(), info);
    }

    public void process(Map<String, ServerInfo> map, File file) {
        try {
            List<String> list = Files.readAllLines(file.toPath());
            for (String line : list) {
                process(map, file, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
