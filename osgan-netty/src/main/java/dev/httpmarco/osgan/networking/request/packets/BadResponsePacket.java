package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public class BadResponsePacket extends Packet {
    private final String id;
    private final UUID uniqueId;
    private final String message;

    public BadResponsePacket(String id, UUID uniqueId, String message) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.message = message;

        this.getBuffer().writeString(this.id)
                .writeUniqueId(this.uniqueId)
                .writeString(this.message);
    }

    public BadResponsePacket(CodecBuffer buffer) {
        super(buffer);

        this.id = buffer.readString();
        this.uniqueId = buffer.readUniqueId();
        this.message = buffer.readString();
    }
}
