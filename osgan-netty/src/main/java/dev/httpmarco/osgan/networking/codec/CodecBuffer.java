package dev.httpmarco.osgan.networking.codec;

import io.netty5.buffer.Buffer;
import lombok.AllArgsConstructor;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class CodecBuffer {

    private final Buffer buffer;

    public CodecBuffer writeString(String value) {
        var bytes = value.getBytes(StandardCharsets.UTF_8);
        this.buffer.writeInt(bytes.length);
        this.buffer.writeBytes(bytes);
        return this;
    }

    public String readString() {
        return this.buffer.readCharSequence(this.buffer.readInt(), StandardCharsets.UTF_8).toString();
    }

    public void resetBuffer() {
        if (buffer.readableBytes() > 0) {
            buffer.skipReadableBytes(buffer.readableBytes());
        }
    }
}