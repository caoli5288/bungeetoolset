package com.i5mc.bungee.channel;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created on 17-3-15.
 */
@Getter
public class ChannelMessage {

    public static final String CHANNEL = "i5channel";

    @Setter
    private String label;
    @Setter
    private String receiver;
    @Setter
    private byte[] buf;
    @Setter
    private boolean queued;

    int sent;
    String sender;

    public static ChannelMessage decode(byte[] input) throws IOException {
        val buf = new DataInputStream(new ByteArrayInputStream(input));
        val out = new ChannelMessage();
        try {
            out.label = buf.readUTF();
            out.receiver = buf.readUTF();
            out.buf = new byte[buf.readShort()];
            buf.readFully(out.buf);
            out.queued = buf.readBoolean();
            out.sent = buf.readInt();
            out.sender = buf.readUTF();
        } catch (IOException e) {
            throw new IOException("decode", e);
        }
        return out;
    }

    public byte[] encode() {
        $.valid($.nil(label), "null");
        $.valid($.nil(buf), "null");
        val out = ByteStreams.newDataOutput();
        out.writeUTF(label);
        out.writeUTF($.nil(receiver) ? "" : receiver);
        out.writeShort(buf.length);
        out.write(buf);
        out.writeInt(sent);
        out.writeUTF($.nil(sender) ? "" : sender);
        return out.toByteArray();
    }

}
