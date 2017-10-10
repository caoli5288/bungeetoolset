package com.i5mc.bungee.list.rt;

import com.i5mc.bungee.list.rt.protocol.Dist;
import com.i5mc.bungee.list.rt.protocol.Heartbeat;
import com.i5mc.bungee.list.rt.protocol.Pull;
import com.i5mc.bungee.list.rt.protocol.PullReq;
import lombok.SneakyThrows;
import lombok.val;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    public static IDataPacket input(InputStream st) {
        val input = new DataInputStream(st);
        val p = valueOf(input.readUTF()).fct.get();
        p.input(input);
        return p;
    }

    @SneakyThrows
    public static void output(OutputStream st, IDataPacket out) {
        val buf = new DataOutputStream(st);
        buf.writeUTF(out.protocol());
        out.output(buf);
        buf.flush();
    }

}
