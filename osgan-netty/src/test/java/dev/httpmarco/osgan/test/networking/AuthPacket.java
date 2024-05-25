package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class AuthPacket extends Packet {
    private String test;
    private  int test1;

    @Override
    public void onRead(CodecBuffer buffer) {
        this.test = buffer.readString();
        this.test1 = buffer.readInt();
    }

    @Override
    public void onWrite(CodecBuffer buffer) {
        buffer.writeString(test);
        buffer.writeInt(test1);
    }
}
