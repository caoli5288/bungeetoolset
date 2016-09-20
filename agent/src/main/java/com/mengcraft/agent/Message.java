package com.mengcraft.agent;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16-9-8.
 */
public class Message {

    public static final String CHANNEL = "BungeeAgent";

    private final List<String> commandList;

    private Message(List<String> commandList) {
        this.commandList = commandList;
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public byte[] encode() {
        if (commandList.size() < 1) {
            throw new IllegalArgumentException(commandList.toString());
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeShort(commandList.size());
        for (String line : commandList) {
            out.writeUTF(line);
        }
        return out.toByteArray();
    }

    public static byte[] encode(List<String> in) {
        return new Message(in).encode();
    }

    public static Message decode(byte[] in) {
        ByteArrayDataInput input = ByteStreams.newDataInput(in);
        int len = input.readShort();
        ArrayList<String> out = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            out.add(input.readUTF());
        }
        return new Message(out);
    }

}
