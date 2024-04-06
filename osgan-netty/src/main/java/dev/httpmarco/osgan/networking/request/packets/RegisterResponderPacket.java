package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public class RegisterResponderPacket extends Packet {
    private String id;

    public RegisterResponderPacket(String id) {
        this.id = id;

        this.getBuffer().writeString(this.id);
    }

    public RegisterResponderPacket(CodecBuffer buffer) {
        super(buffer);

        this.id = buffer.readString();
    }
}
