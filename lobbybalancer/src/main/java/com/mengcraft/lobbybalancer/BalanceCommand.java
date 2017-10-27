package com.mengcraft.lobbybalancer;

import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.util.Queue;

public class BalanceCommand extends Command {

    public BalanceCommand() {
        super("balance");
    }

    @Override
    public void execute(CommandSender who, String[] input) {
        if (who instanceof UserConnection) {
            if (input.length > 1) {
                who.sendMessage(ChatColor.RED + "/balance [target]");
            } else {
                val p = (UserConnection) who;
                process(p, input.length == 1 ? input[0] : null);
            }
        } else {
            if (input.length < 1) {
                who.sendMessage(ChatColor.RED + "/balance <player> [target]");
            } else {
                val p = (UserConnection) BungeeCord.getInstance().getPlayer(input[0]);
                if (!$.nil(p)) {
                    process(p, input.length == 2 ? input[1] : null);
                }
            }
        }
    }

    private void process(UserConnection p, String target) {
        Zone zone = ZoneMgr.select($.nil(target) ? p.getServer().getInfo().getName() : target);
        if (!$.nil(zone)) {
            Queue<String> alive = zone.alive();
            String head = alive.peek();
            if (!$.nil(head)) {
                ServerInfo info = InfoMgr.getByName(head).getServerInfo();
                p.setServerJoinQueue(alive);
                p.connect(info, null, true);
            }
        }
    }

}
