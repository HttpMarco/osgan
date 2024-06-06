package dev.httpmarco.osgan.networking.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class BadRequestPacket extends Packet {

    private UUID uuid;

    @Override
    public void read(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUniqueId(uuid);
    }
}
