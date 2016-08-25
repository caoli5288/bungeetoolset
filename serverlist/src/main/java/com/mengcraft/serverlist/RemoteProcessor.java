package com.mengcraft.serverlist;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

/**
 * Created on 16-8-25.
 */
public class RemoteProcessor implements Processor {

    public static final Processor INSTANCE = new RemoteProcessor();

    private RemoteProcessor() {
    }

    public void process(Map<String, ServerInfo> map, File file) {
        Properties info = new Properties();
        try {
            info.load(new FileReader(file));
            process(map, info);
        } catch (IOException ignore) {
        }
    }

    private void process(Map<String, ServerInfo> map, Properties info) {
        try {
            Class.forName(info.getProperty("driver"));
            try (Connection conn = DriverManager.getConnection(info.getProperty("url"), info);
                 Statement stat = conn.createStatement();
                 ResultSet query = stat.executeQuery("select name,host,port,restricted from " + info.getProperty("table"))
            ) {
                process(map, query);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void process(Map<String, ServerInfo> map, ResultSet query) throws SQLException {
        while (query.next()) {
            add(new BungeeServerInfo(
                    query.getString(1),
                    new InetSocketAddress(query.getString(2), query.getInt(3)),
                    "",
                    query.getBoolean(4)
            ), map);
        }
    }

    private void add(BungeeServerInfo info, Map<String, ServerInfo> map) {
        map.put(info.getName(), info);
    }

}