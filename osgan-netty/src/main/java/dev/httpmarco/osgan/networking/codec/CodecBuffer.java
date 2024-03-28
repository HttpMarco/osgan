package dev.httpmarco.osgan.networking.codec;

import io.netty5.buffer.Buffer;
import lombok.AllArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
            System.err.println("Buffer not empty. Remaining bytes: " + buffer.readableBytes());
            buffer.skipReadableBytes(buffer.readableBytes());
        }
    }

    public CodecBuffer writeBoolean(Boolean booleanValue) {
        this.buffer.writeBoolean(booleanValue);
        return this;
    }

    public boolean readBoolean() {
        return this.buffer.readBoolean();
    }

    public CodecBuffer writeUniqueId(UUID uniqueId) {
        this.buffer.writeLong(uniqueId.getMostSignificantBits());
        this.buffer.writeLong(uniqueId.getLeastSignificantBits());
        return this;
    }

    public UUID readUniqueId() {
        return new UUID(this.buffer.readLong(), this.buffer.readLong());
    }

    public CodecBuffer writeInt(int value) {
        this.buffer.writeInt(value);
        return this;
    }

    public int readInt() {
        return this.buffer.readInt();
    }

    public CodecBuffer writeEnum(Enum<?> value) {
        this.buffer.writeInt(value.ordinal());
        return this;
    }

    public <T extends Enum<?>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[this.buffer.readInt()];
    }


    public CodecBuffer writeLong(long value) {
        this.buffer.writeLong(value);
        return this;
    }

    public long readLong() {
        return this.buffer.readLong();
    }

    public CodecBuffer writeFloat(float value) {
        this.buffer.writeFloat(value);
        return this;
    }

    public float readFloat() {
        return this.buffer.readFloat();
    }

    public CodecBuffer writeDouble(double value) {
        this.buffer.writeDouble(value);
        return this;
    }

    public double readDouble() {
        return this.buffer.readDouble();
    }

    public short readShort() {
        return this.buffer.readShort();
    }

    public CodecBuffer writeShort(short value) {
        this.buffer.writeShort(value);
        return this;
    }

    public CodecBuffer writeByte(byte value) {
        this.buffer.writeByte(value);
        return this;
    }

    public byte readByte() {
        return this.buffer.readByte();
    }
}