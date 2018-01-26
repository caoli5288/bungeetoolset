package com.i5mc.bungee.list;

import com.i5mc.bungee.list.rt.RT;
import com.i5mc.bungee.list.rt.RTServer;
import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * Created on 17-7-20.
 */
public class CommandExec extends Command {

    public CommandExec() {
        super("listd", "listd.admin");
    }

    private enum Exec {

        FILTER((p, itr) -> {
            Pattern pattern = Pattern.compile(itr.next() + "-(.)+");
            BungeeCord.getInstance().getServers().forEach((id, info) -> {
                if (!pattern.matcher(id).matches()) {
                    return;
                }
                p.sendMessage("- id: " + id + info.getAddress());
                Collection<ProxiedPlayer> all = info.getPlayers();
                if (!all.isEmpty()) {
                    p.sendMessage("  p: " + all.size());
                    p.sendMessage("  p_list: " + $.join(all, i -> i.getName(), ", "));
                }
            });
        }),

        RT_DIST_RELOAD((p, itr) -> {
            if (!RT.INSTANCE.isListen()) throw new IllegalStateException("not running");
            try {
                RT.load(Main.i);
                if (!RT.INSTANCE.isListen()) {
                    RTServer.log("RT server shutdown");
                    RTServer.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            p.sendMessage(ChatColor.GREEN + "Okay");
        });

        private final BiConsumer func;

        Exec(BiConsumer<CommandSender, Iterator<String>> func) {
            this.func = func;
        }
    }

    public void execute(CommandSender who, String[] input) {
        val itr = Arrays.asList(input).iterator();
        try {
            Exec.valueOf(itr.next().toUpperCase().replace('-', '_')).func.accept(who, itr);
        } catch (IllegalArgumentException ign) {
        } catch (NoSuchElementException e) {
            for (Exec i : Exec.values()) {
                who.sendMessage("/listd " + i.name().toLowerCase().replace('_', '-'));
            }
        } catch (Exception e) {
            who.sendMessage(ChatColor.RED + e.toString());
        }
    }

}
