package com.i5mc.bungee.list.rt.protocol;

import com.google.common.collect.ImmutableList;
import com.i5mc.bungee.list.ListHelper;
import com.i5mc.bungee.list.rt.IDataPacket;
import com.i5mc.bungee.list.rt.Protocol;
import com.i5mc.bungee.list.rt.RTInfoMgr;
import com.i5mc.bungee.list.rt.RTServer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.Socket;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created on 17-7-19.
 */
@Data
@NoArgsConstructor
public class Pull implements IDataPacket {

    private String group;
    private boolean full;

    public Pull(String group) {
        this.group = group;
    }

    public Pull(String group, boolean full) {
        this.group = group;
        this.full = full;
    }

    @Override
    public String protocol() {
        return Protocol.PULL.name();
    }

    @Override
    @SneakyThrows
    public void exec(Socket so) {
        val alive = full ? find(Pattern.compile(group + "-\\w+")) : RTInfoMgr.alive(group);
        List<PullRes.Res> l = ListHelper.collect(alive, i -> new PullRes.Res(i.getName(), i.getAddress()));
        val p = new PullRes(l);
        Protocol.output(so.getOutputStream(), p);
        RTServer.log(this);
    }

    public static List<ServerInfo> find(Pattern pattern) {
        val b = ImmutableList.<ServerInfo>builder();
        BungeeCord.getInstance().getServers().forEach((name, info) -> {
            if (pattern.matcher(name).matches()) {
                b.add(info);
            }
        });
        return b.build();
    }

    @SneakyThrows
    public void input(DataInput input) {
        group = input.readUTF();
        full = input.readBoolean();
    }

    @SneakyThrows
    public void output(DataOutput output) {
        output.writeUTF(group);
        output.writeBoolean(full);
    }

}
