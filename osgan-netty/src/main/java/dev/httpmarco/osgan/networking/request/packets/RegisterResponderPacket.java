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
public class RegisterResponderPacket extends Packet {
    private String id;

    @Override
    public void onRead(CodecBuffer buffer) {

    }

    @Override
    public void onWrite(CodecBuffer buffer) {

    }
}
