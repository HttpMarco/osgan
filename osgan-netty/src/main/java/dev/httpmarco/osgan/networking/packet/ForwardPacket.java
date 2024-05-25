package dev.httpmarco.osgan.networking.packet;

import dev.httpmarco.osgan.networking.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class ForwardPacket extends Packet {
    private final String id;
    private final String className;
    private final String packetJson;
}
