package com.mengcraft.querystat;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 16-9-10.
 */
public class CommandExecutor extends Command {


    private final ProxyServer server;

    public CommandExecutor(ProxyServer server) {
        super("querystat");
        this.server = server;
    }

    @Override
    public void execute(CommandSender sender, String[] j) {
        if (sender instanceof ConsoleCommandSender) {
            try {
                execute(sender, Lists.newArrayList(j).iterator());
            } catch (IOException ignore) {
                ignore.printStackTrace();
            }
        }
    }

    private void execute(CommandSender sender, Iterator<String> it) throws IOException {
        if (it.hasNext() && it.next().equals("export")) {
            BufferedWriter writer = new BufferedWriter(new FileWriter("querystat.csv", false));
            writer.write("\"id\",\"name\",\"addr\",\"online\",\"max\",\"motd\"");
            writer.newLine();
            writer.flush();
            AtomicInteger id = new AtomicInteger();
            server.getServers().forEach((name, info) -> {
                write(writer, id, info);
            });
            sender.sendMessage(ChatColor.GREEN + "Exporting to querystat.csv...");
        } else {
            sender.sendMessage(ChatColor.RED + "/querystat export");
        }
    }

    private void write(BufferedWriter writer, AtomicInteger id, ServerInfo info) {
        info.ping((result, err) -> {
            try {
                synchronized (writer) {
                    writer.write("\"" + id.incrementAndGet() + "\"");
                    writer.write(",");
                    writer.write("\"" + info.getName() + "\"");
                    writer.write(",");
                    writer.write("\"" + info.getAddress() + "\"");
                    if (err == null) {
                        writer.write(",");
                        writer.write("\"" + result.getPlayers().getOnline() + "\"");
                        writer.write(",");
                        writer.write("\"" + result.getPlayers().getMax() + "\"");
                        writer.write(",");
                        writer.write("\"" + result.getDescription() + "\"");
                    }
                    writer.newLine();
                    writer.flush();
                }
            } catch (IOException ignore) {
            }
        });
    }

}
