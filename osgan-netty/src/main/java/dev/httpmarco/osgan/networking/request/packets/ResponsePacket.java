package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public class ResponsePacket extends Packet {
    private final UUID uniqueId;
    private final String packetJson;

    public ResponsePacket(UUID uniqueId, String packetJson) {
        this.uniqueId = uniqueId;
        this.packetJson = packetJson;

        this.getBuffer() .writeUniqueId(this.uniqueId)
                .writeString(this.packetJson);
    }

    public ResponsePacket(CodecBuffer buffer) {
        super(buffer);

        this.uniqueId = buffer.readUniqueId();
        this.packetJson = buffer.readString();
    }
}
