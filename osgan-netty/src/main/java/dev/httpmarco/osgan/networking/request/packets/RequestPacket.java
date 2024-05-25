package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class RequestPacket extends Packet {
    private String id;
    private UUID uniqueId;
    private String properties;

    @Override
    public void onRead(CodecBuffer buffer) {
        this.id = buffer.readString();
        this.uniqueId = buffer.readUniqueId();
        this.properties = buffer.readString();
    }

    @Override
    public void onWrite(CodecBuffer buffer) {
        buffer.writeString(id);
        buffer.writeUniqueId(uniqueId);
        buffer.writeString(properties);
    }
}
