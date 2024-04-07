package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.files.json.JsonObjectSerializer;
import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
public class RequestPacket extends Packet {
    private final String id;
    private final UUID uniqueId;
    private final JsonObjectSerializer properties;

    public RequestPacket(String id, UUID uniqueId, JsonObjectSerializer properties) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.properties = properties;

        this.getBuffer().writeString(this.id)
                .writeUniqueId(this.uniqueId)
                .writeJsonDocument(this.properties);
    }

    public RequestPacket(CodecBuffer buffer) {
        super(buffer);

        this.id = buffer.readString();
        this.uniqueId = buffer.readUniqueId();
        this.properties = buffer.readJsonDocument();
    }
}
