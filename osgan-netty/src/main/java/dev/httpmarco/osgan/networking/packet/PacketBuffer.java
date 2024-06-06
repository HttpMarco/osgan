package dev.httpmarco.osgan.networking.packet;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferAllocator;
import io.netty5.buffer.DefaultBufferAllocators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@AllArgsConstructor
public final class PacketBuffer {
    private static final BufferAllocator BUFFER_ALLOCATOR = DefaultBufferAllocators.offHeapAllocator();

    @Getter
    private final Buffer origin;

    public static PacketBuffer allocate() {
        return new PacketBuffer(BUFFER_ALLOCATOR.allocate(0));
    }

    public void resetBuffer() {
        if (origin.readableBytes() > 0) {
            System.err.println("Buffer not empty! Skipping remaining bytes: " + origin.readableBytes());
            origin.skipReadableBytes(origin.readableBytes());
        }
    }

    public PacketBuffer writeString(String value) {
        var bytes = value.getBytes(StandardCharsets.UTF_8);
        this.origin.writeInt(bytes.length);
        this.origin.writeBytes(bytes);
        return this;
    }

    public String readString() {
        return this.origin.readCharSequence(this.origin.readInt(), StandardCharsets.UTF_8).toString();
    }

    public PacketBuffer writeBoolean(Boolean booleanValue) {
        this.origin.writeBoolean(booleanValue);
        return this;
    }

    public boolean readBoolean() {
        return this.origin.readBoolean();
    }

    public PacketBuffer writeUniqueId(UUID uniqueId) {
        this.origin.writeLong(uniqueId.getMostSignificantBits());
        this.origin.writeLong(uniqueId.getLeastSignificantBits());
        return this;
    }

    public UUID readUniqueId() {
        return new UUID(this.origin.readLong(), this.origin.readLong());
    }

    public PacketBuffer writeInt(int value) {
        this.origin.writeInt(value);
        return this;
    }

    public int readInt() {
        return this.origin.readInt();
    }

    public PacketBuffer writeEnum(Enum<?> value) {
        this.origin.writeInt(value.ordinal());
        return this;
    }

    public <T extends Enum<?>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[this.origin.readInt()];
    }


    public <T> void writeList(@NotNull List<T> list, BiConsumer<PacketBuffer, T> consumer) {
        this.writeInt(list.size());

        list.forEach(o -> consumer.accept(this, o));
    }

    public <T> List<T> readList(List<T> list, Supplier<T> supplier) {
        var size = this.readInt();

        for (int i = 0; i < size; i++) {
            list.add(supplier.get());
        }

        return list;
    }

    public void writeBuffer(PacketBuffer buffer) {
        this.writeInt(buffer.getOrigin().readableBytes());
        this.writeBytes(buffer.getOrigin());
    }

    public PacketBuffer writeLong(long value) {
        this.origin.writeLong(value);
        return this;
    }

    public long readLong() {
        return this.origin.readLong();
    }

    public PacketBuffer writeFloat(float value) {
        this.origin.writeFloat(value);
        return this;
    }

    public float readFloat() {
        return this.origin.readFloat();
    }

    public PacketBuffer writeDouble(double value) {
        this.origin.writeDouble(value);
        return this;
    }

    public double readDouble() {
        return this.origin.readDouble();
    }

    public short readShort() {
        return this.origin.readShort();
    }

    public PacketBuffer writeShort(short value) {
        this.origin.writeShort(value);
        return this;
    }

    public PacketBuffer writeByte(byte value) {
        this.origin.writeByte(value);
        return this;
    }

    public byte readByte() {
        return this.origin.readByte();
    }

    public PacketBuffer writeBytes(Buffer bytes) {
        this.origin.writeBytes(bytes);
        return this;
    }

    public PacketBuffer writeBytes(byte[] bytes) {

        this.origin.writeInt(bytes.length);

        for (byte b : bytes) {
            this.origin.writeByte(b);
        }
        return this;
    }

    public byte[] readBytes() {
        var elements = new byte[this.origin.readInt()];

        for (int i = 0; i < elements.length; i++) {
            elements[i] = this.origin.readByte();
        }
        return elements;
    }
}