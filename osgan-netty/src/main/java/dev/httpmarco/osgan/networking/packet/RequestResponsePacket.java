package dev.httpmarco.osgan.networking.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class RequestResponsePacket extends Packet {

    private UUID uuid;
    private Packet response;

    @Override
    @SneakyThrows
    public void read(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();

        this.response = (Packet) PacketAllocator.allocate(Class.forName(buffer.readString()));
        this.response.read(buffer);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUniqueId(uuid);
        buffer.writeString(response.getClass().getName());

        response.write(buffer);
    }
}
