package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@ToString
@Accessors(fluent = true)
@AllArgsConstructor
public class testpacket extends Packet {

    private String name;
    private UUID uniqueId;
    private long time;

    @Override
    public void read(PacketBuffer buffer) {
        this.name = buffer.readString();
        this.uniqueId = buffer.readUniqueId();
        this.time = buffer.readLong();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(name);
        buffer.writeUniqueId(uniqueId);
        buffer.writeLong(time);
    }
}
