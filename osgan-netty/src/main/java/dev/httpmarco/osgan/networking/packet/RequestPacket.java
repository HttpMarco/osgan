package dev.httpmarco.osgan.networking.packet;

import dev.httpmarco.osgan.networking.CommunicationProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public final class RequestPacket extends Packet {

    private String id;
    private UUID uuid;

    //todo
    private CommunicationProperty property;

    @Override
    @SneakyThrows
    public void read(@NotNull PacketBuffer buffer) {
        this.id = buffer.readString();
        this.uuid = buffer.readUniqueId();

        this.property = CommunicationProperty.class.getConstructor().newInstance();
        this.property.read(buffer);
    }

    @Override
    public void write(@NotNull PacketBuffer buffer) {
        buffer.writeString(id);
        buffer.writeUniqueId(uuid);

        property.write(buffer);
    }
}
