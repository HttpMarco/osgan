package dev.httpmarco.osgan.networking.packet;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ChannelTransmitAuthPacket extends Packet {
    private final String id;

    public ChannelTransmitAuthPacket(String id) {
        super();

        this.id = id;

        this.getBuffer().writeString(this.id);
    }

    public ChannelTransmitAuthPacket(CodecBuffer buffer) {
        super(buffer);

        this.id = buffer.readString();
    }
}
