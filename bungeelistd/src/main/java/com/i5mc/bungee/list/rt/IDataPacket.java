package com.i5mc.bungee.list.rt;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.Socket;

/**
 * Created on 17-7-19.
 */
public interface IDataPacket {

    String protocol();

    void exec(Socket so);

    void input(DataInput input);

    void output(DataOutput output);
}
