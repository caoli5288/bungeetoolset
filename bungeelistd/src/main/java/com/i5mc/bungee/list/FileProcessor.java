package com.i5mc.bungee.list;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created on 16-8-25.
 */
public class FileProcessor extends Processor {

    public static final Processor INSTANCE = new FileProcessor();

    private FileProcessor() {
    }

    public boolean accept(File f) {
        return f.getName().endsWith(".list");
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

    private boolean process(Map<String, ServerInfo> in, File file, String line) {
        StringTokenizer it = new StringTokenizer(line);
        try {
            String token = it.nextToken();
            if (token.charAt(0) == '#') {
                return false;// ignore this line
            }
            if (token.equals("server")) {
                put(in, new BungeeServerInfo(
                        it.nextToken(),
                        toAddress(it.nextToken(), it.nextToken()),
                        "",
                        it.hasMoreTokens() && it.nextToken().equals("restricted")
                ));
                return true;
            }
            throw new RuntimeException("unknown token " + token + " in " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
