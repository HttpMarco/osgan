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
@AllArgsConstructor
public class RequestPacket extends Packet {
    private final String id;
    private final UUID uniqueId;
    private final JsonObjectSerializer properties;
}
