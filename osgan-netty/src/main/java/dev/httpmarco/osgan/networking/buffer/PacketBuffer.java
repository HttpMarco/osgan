package dev.httpmarco.osgan.networking.buffer;

import io.netty5.buffer.Buffer;
import lombok.AllArgsConstructor;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class PacketBuffer {

    private final Buffer buffer;

    public PacketBuffer writeString(String value) {
        var bytes = value.getBytes(StandardCharsets.UTF_8);
        this.buffer.writeInt(bytes.length);
        this.buffer.writeBytes(bytes);
        return this;
    }

    public String readString() {
        return this.buffer.readCharSequence(this.buffer.readInt(), StandardCharsets.UTF_8).toString();
    }
}