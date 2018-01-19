package com.i5mc.bungee.list.rt.protocol;

import com.google.common.collect.ImmutableList;
import com.i5mc.bungee.list.rt.IDataPacket;
import com.i5mc.bungee.list.rt.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created on 17-7-19.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class PullRes implements IDataPacket {

    @AllArgsConstructor
    @Data
    public static class Res {

        private String name;
        private String host;
        private int port;

        public Res(String name, InetSocketAddress add) {
            this(name, add.getAddress().getHostAddress(), add.getPort());
        }
    }

    private List<Res> alive;

    @Override
    public String protocol() {
        return Protocol.PULL_RES.name();
    }

    @Override
    public void exec(Socket so) {
    }

    @SneakyThrows
    public void input(DataInput input) {
        ImmutableList.Builder<Res> b = ImmutableList.builder();
        int len = input.readInt();
        for (int i = 0; i < len; i++) {
            b.add(new Res(input.readUTF(), input.readUTF(), input.readInt()));
        }
        alive = b.build();
    }

    @SneakyThrows
    public void output(DataOutput output) {
        output.writeInt(alive.size());
        for (Res req : alive) {
            output.writeUTF(req.name);
            output.writeUTF(req.host);
            output.writeInt(req.port);
        }
    }

}
