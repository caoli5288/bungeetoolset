package com.i5mc.bungee.list.rt.protocol;

import com.i5mc.bungee.list.rt.IDataPacket;
import com.i5mc.bungee.list.rt.Protocol;
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
public class Heartbeat implements IDataPacket {

    private String group;
    private String host;
    private int port;
    private boolean fixedId;

    @Override
    public String protocol() {
        return Protocol.HEARTBEAT.name();
    }

    @SneakyThrows
    public void exec(Socket so) {
        RTServer.distribute(new Dist(group, host, port, fixedId));
    }

    @SneakyThrows
    public void input(DataInput input) {
        group = input.readUTF();
        host = input.readUTF();
        port = input.readInt();
        fixedId = input.readBoolean();
    }

    @SneakyThrows
    public void output(DataOutput output) {
        output.writeUTF(group);
        output.writeUTF(host);
        output.writeInt(port);
        output.writeBoolean(fixedId);
    }

}
