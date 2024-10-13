package dev.httpmarco.osgan.networking.packet;

import dev.httpmarco.osgan.networking.ClassSupplier;
import dev.httpmarco.osgan.networking.CommunicationListener;
import dev.httpmarco.osgan.networking.DefaultClassSupplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
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
    public Packet buildPacket(ClassSupplier classSupplier) {
        try {
            Packet packet = (Packet) PacketAllocator.allocate(classSupplier.classByName(this.packetClass));
            Objects.requireNonNull(packet).read(this.buffer);
            return packet;
        } catch (ClassNotFoundException e) {
            CommunicationListener.getLogger().log(Level.SEVERE, MessageFormat.format("Could not find supplied packet class: {0}", this.packetClass));

            if (classSupplier instanceof DefaultClassSupplier) {
                CommunicationListener.getLogger().log(Level.SEVERE, "You are using the default class supplier. If the provided packet class originates from " +
                        "another project or classpath, create a ClassSupplier to access the classes there (CommunicationListener#setClassSupplier)");
            }

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
