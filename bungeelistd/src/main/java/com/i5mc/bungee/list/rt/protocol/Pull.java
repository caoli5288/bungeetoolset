package com.i5mc.bungee.list.rt.protocol;

import com.i5mc.bungee.list.ListHelper;
import com.i5mc.bungee.list.rt.IDataPacket;
import com.i5mc.bungee.list.rt.Protocol;
import com.i5mc.bungee.list.rt.RTInfoMgr;
import com.i5mc.bungee.list.rt.RTServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.Socket;
import java.util.List;

/**
 * Created on 17-7-19.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Pull implements IDataPacket {

    private String group;

    @Override
    public String protocol() {
        return Protocol.PULL.name();
    }

    @Override
    @SneakyThrows
    public void exec(Socket so) {
        val alive = RTInfoMgr.alive(group);
        List<PullReq.Req> l = ListHelper.collect(alive, i -> new PullReq.Req(i.getName(), i.getAddress()));
        val p = new PullReq(l);
        Protocol.output(so.getOutputStream(), p);
        RTServer.log(this);
    }

    @SneakyThrows
    public void input(DataInput input) {
        group = input.readUTF();
    }

    @SneakyThrows
    public void output(DataOutput output) {
        output.writeUTF(group);
    }

}
