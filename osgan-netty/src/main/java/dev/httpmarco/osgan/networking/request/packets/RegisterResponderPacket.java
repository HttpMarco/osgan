package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.networking.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class RegisterResponderPacket implements Packet {
    private String id;
}
