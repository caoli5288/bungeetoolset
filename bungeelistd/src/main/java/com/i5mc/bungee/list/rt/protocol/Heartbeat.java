package com.i5mc.bungee.list.rt.protocol;

import com.i5mc.bungee.list.rt.IDataPacket;
import com.i5mc.bungee.list.rt.Protocol;
import com.i5mc.bungee.list.rt.RT;
import com.i5mc.bungee.list.rt.RTServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.InetSocketAddress;
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

    @Override
    public String protocol() {
        return Protocol.HEARTBEAT.name();
    }

    @SneakyThrows
    public void exec(Socket so) {
        val d = new Dist(group, host, port);
        for (val dist : RTServer.getDist()) {
            RTServer.exec(() -> send(d, dist));
        }
        RTServer.log(this);
    }

    @SneakyThrows
    private void send(Dist packet, String to) {
        val cli = new Socket();
        cli.setSoTimeout(4000);
        cli.connect(new InetSocketAddress(to, RT.PORT));
        Protocol.send(cli, packet);
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
