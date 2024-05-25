package dev.httpmarco.osgan.networking.request.packets;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.codec.CodecBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class BadResponsePacket extends Packet {
    private final String id;
    private final UUID uniqueId;
    private final String message;

    @Override
    public void onRead(CodecBuffer buffer) {

    }

    @Override
    public void onWrite(CodecBuffer buffer) {

    }
}
