package dev.httpmarco.osgan.networking.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class RegisterResponderPacket extends Packet {
    private String id;

    @Override
    public void read(PacketBuffer buffer) {
        this.id = buffer.readString();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(id);
    }
}
