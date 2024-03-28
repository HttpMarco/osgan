package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.files.json.JsonObjectSerializer;
import dev.httpmarco.osgan.networking.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class RequestPacket implements Packet {
    private String id;
    private UUID uniqueId;
    private JsonObjectSerializer properties;
}
