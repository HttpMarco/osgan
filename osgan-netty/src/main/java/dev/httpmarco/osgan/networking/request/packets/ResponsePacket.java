package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import dev.httpmarco.osgan.reflections.common.Allocator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class ResponsePacket extends Packet {
    private UUID uniqueId;
    private Packet packet;

    @Override
    @SneakyThrows
    public void onRead(CodecBuffer buffer) {
        this.uniqueId = buffer.readUniqueId();

        var className = buffer.readString();
        this.packet = (Packet) Allocator.allocate(Class.forName(className));

        assert packet != null;
        packet.onRead(buffer);
    }

    @Override
    public void onWrite(CodecBuffer buffer) {
        buffer.writeUniqueId(uniqueId);
        buffer.writeString(packet.getClass().getName());
        packet.onWrite(buffer);
    }
}
