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

    public static final String CHANNEL = "Agent";

    private final Executor executor;
    private final List<String> command;
    private final boolean queued;


    private Message(Executor executor, List<String> command, boolean queued) {
        this.executor = executor;
        this.command = command;
        this.queued = queued;
    }

    public List<String> getCommand() {
        return command;
    }

    public Executor getExecutor() {
        return executor;
    }

    public boolean isQueued() {
        return queued;
    }

    public byte[] encode() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeByte(executor.ordinal());
        out.writeByte(command.size());
        for (String line : command) {
            out.writeUTF(line);
        }
        out.writeBoolean(queued);
        return out.toByteArray();
    }

    public static Message decode(byte[] in) {
        ByteArrayDataInput input = ByteStreams.newDataInput(in);
        Executor executor = Executor.get(input.readByte());

        int len = input.readByte();
        ArrayList<String> out = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            out.add(input.readUTF());
        }

        return new Message(executor, out, input.readBoolean());
    }

    public static Message get(Executor executor, List<String> command, boolean queued) {
        return new Message(executor, new ArrayList<>(command), queued);
    }

}
