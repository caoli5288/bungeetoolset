package com.i5mc.bungee.list.rt.protocol;

import com.i5mc.bungee.list.rt.IDataPacket;
import com.i5mc.bungee.list.rt.Protocol;
import com.i5mc.bungee.list.rt.RTInfoMgr;
import com.i5mc.bungee.list.rt.RTServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.Socket;

/**
 * Created on 17-7-19.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Dist implements IDataPacket {

    private String group;
    private String host;
    private int port;

    @Override
    public String protocol() {
        return Protocol.DIST.name();
    }

    @Override
    public void exec(Socket so) {
        RTInfoMgr.alive(group, host, port);
        RTServer.log(this);
    }

    @SneakyThrows
    public void input(DataInput input) {
        group = input.readUTF();
        host = input.readUTF();
        port = input.readInt();
    }

    @SneakyThrows
    public void output(DataOutput output) {
        output.writeUTF(group);
        output.writeUTF(host);
        output.writeInt(port);
    }
}
