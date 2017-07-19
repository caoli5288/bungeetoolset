package com.i5mc.bungee.list.rt;

import com.i5mc.bungee.list.rt.protocol.Dist;
import com.i5mc.bungee.list.rt.protocol.Heartbeat;
import com.i5mc.bungee.list.rt.protocol.Pull;
import com.i5mc.bungee.list.rt.protocol.PullReq;
import lombok.SneakyThrows;
import lombok.val;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.function.Supplier;

/**
 * Created on 17-7-19.
 */
public enum Protocol {

    HEARTBEAT(() -> new Heartbeat()),
    DIST(() -> new Dist()),
    PULL(() -> new Pull()),
    PULL_REQ(() -> new PullReq());

    private final Supplier<IDataPacket> fct;

    Protocol(Supplier<IDataPacket> fct) {
        this.fct = fct;
    }

    @SneakyThrows
    public static IDataPacket input(Socket so) {
        val input = new DataInputStream(so.getInputStream());
        try {
            val p = valueOf(input.readUTF()).fct.get();
            p.input(input);
            p.exec(so);
            return p;
        } catch (IllegalArgumentException ign) {
        }
        return null;
    }

    @SneakyThrows
    public static void send(Socket so, IDataPacket p) {
        val out = new DataOutputStream(so.getOutputStream());
        out.writeUTF(p.protocol());
        p.output(out);
        out.flush();
    }

}
