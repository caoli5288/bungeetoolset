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
public class ListProcessor {

    private final Map<String, ServerInfo> map;
    private final Main main;

    private ListProcessor(Main main, Map<String, ServerInfo> map) {
        this.main = main;
        this.map = map;
    }

    private boolean processLine(File file, String line) {
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
                ));
                return true;
            }
            main.getLogger().warning("!!! " + file + " " + line);
        } catch (Exception e) {
            main.getLogger().warning("!!! " + file + " " + line);
        }
        return false;
    }

    private void add(BungeeServerInfo info) {
        map.put(info.getName(), info);
    }

    private void processFile(File file) {
        try {
            List<String> list = Files.readAllLines(file.toPath());
            for (String line : list) {
                processLine(file, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process() {
        File[] files = Main.DIR.listFiles(j -> j.getName().endsWith(".list"));
        for (File file : files) {
            processFile(file);
        }
    }

    public static void process(Main main, Map<String, ServerInfo> map) {
        new ListProcessor(main, map).process();
    }

}
