package com.i5mc.bungee.list;

import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created on 16-8-25.
 */
public class RemoteProcessor extends Processor {

    public static final Processor INSTANCE = new RemoteProcessor();

    private RemoteProcessor() {
    }

    public boolean accept(File f) {
        return f.getName().endsWith(".remote");
    }

    public void process(Map<String, ServerInfo> map, File file) {
        Properties info = new Properties();
        try {
            info.load(new FileReader(file));
            info.put(".remote", file.getPath());
            process(map, info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, Map> backup = new HashMap<>(); // Avoid SQLException

    private void process(Map<String, ServerInfo> map, Properties info) throws IOException {
        try {
            Class.forName(info.getProperty("driver"));
            try (Connection conn = DriverManager.getConnection(info.getProperty("url"), info)) {
                Statement stat = conn.createStatement();
                ResultSet query = stat.executeQuery("select name,host,port,restricted from " + info.getProperty("table"));
                Map j = new HashMap();
                process(j, query);
                map.putAll(j);

                backup.put(info.getProperty(".remote"), j);// Cache result for backup
            }// Statement and ResultSet closed automatic if Connection closed?
        } catch (ClassNotFoundException | SQLException e) {
            String remote = info.getProperty(".remote");
            boolean backed = backup.containsKey(remote);
            if (backed) {
                map.putAll(backup.get(remote));
            }
            throw new IOException(backed ? "Pick backed up result for " + remote : "Fail to query remote list " + remote, e);
        }
    }

    private void process(Map<String, ServerInfo> in, ResultSet query) throws SQLException {
        while (query.next()) {
            put(in, new BungeeServerInfo(
                    query.getString(1),
                    toAddress(query.getString(2), query.getInt(3)),
                    "",
                    query.getBoolean(4)
            ));
        }
    }

}