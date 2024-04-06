package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public class AuthPacket extends Packet {
    private final String test;
    private final int test1;

    public AuthPacket() {
        this.test = "test123";
        this.test1 = 123;

        this.getBuffer().writeString(this.test).writeInt(this.test1);
    }

    public AuthPacket(CodecBuffer buffer) {
        super(buffer);

        this.test = buffer.readString();
        this.test1 = buffer.readInt();
    }
}
