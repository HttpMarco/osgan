package dev.httpmarco.osgan.networking.packet;

import dev.httpmarco.osgan.networking.CommunicationListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class RequestResponsePacket extends Packet {

    private UUID uuid;
    private String packetClass;
    private PacketBuffer buffer;

    public RequestResponsePacket(UUID uuid, Packet packet) {
        this.uuid = uuid;
        this.packetClass = packet.getClass().getName();
        this.buffer = PacketBuffer.allocate();
        packet.write(this.buffer);
    }

    @Nullable
    public Packet buildPacket() {
        try {
            Packet packet = (Packet) PacketAllocator.allocate(Class.forName(this.packetClass));
            Objects.requireNonNull(packet).read(this.buffer);
            return packet;
        } catch (ClassNotFoundException e) {
            CommunicationListener.getLogger().log(Level.SEVERE, MessageFormat.format("Could not find supplied packet class: {0}", this.packetClass));
            return null;
        }
    }

    @Override
    @SneakyThrows
    public void read(PacketBuffer buffer) {
        this.uuid = buffer.readUniqueId();
        this.packetClass = buffer.readString();
        this.buffer = new PacketBuffer(buffer.getOrigin().copy());
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUniqueId(uuid);
        buffer.writeString(packetClass);

        buffer.writeBytes(this.buffer.getOrigin());
    }
}
