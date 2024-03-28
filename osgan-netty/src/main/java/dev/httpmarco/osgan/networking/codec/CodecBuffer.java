package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.files.json.JsonObjectSerializer;
import io.netty5.buffer.Buffer;
import io.netty5.buffer.BufferAllocator;
import io.netty5.buffer.DefaultBufferAllocators;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
public class CodecBuffer {
    private static final BufferAllocator BUFFER_ALLOCATOR = DefaultBufferAllocators.offHeapAllocator();

    @Getter(AccessLevel.PACKAGE)
    private final Buffer origin;

    public static CodecBuffer allocate() {
        return new CodecBuffer(BUFFER_ALLOCATOR.allocate(0));
    }

    public void resetBuffer() {
        if (origin.readableBytes() > 0) {
            System.err.println("Buffer not empty! Skipping remaining bytes: " + origin.readableBytes());
            origin.skipReadableBytes(origin.readableBytes());
        }
    }

    public CodecBuffer writeString(String value) {
        var bytes = value.getBytes(StandardCharsets.UTF_8);
        this.origin.writeInt(bytes.length);
        this.origin.writeBytes(bytes);
        return this;
    }

    public String readString() {
        return this.origin.readCharSequence(this.origin.readInt(), StandardCharsets.UTF_8).toString();
    }

    public CodecBuffer writeJsonDocument(JsonObjectSerializer jsonDocument) {
        this.writeString(jsonDocument.toString());
        return this;
    }

    public JsonObjectSerializer readJsonDocument() {
        return new JsonObjectSerializer(this.readString());
    }

    public CodecBuffer writeBoolean(Boolean booleanValue) {
        this.origin.writeBoolean(booleanValue);
        return this;
    }

    public boolean readBoolean() {
        return this.origin.readBoolean();
    }

    public CodecBuffer writeUniqueId(UUID uniqueId) {
        this.origin.writeLong(uniqueId.getMostSignificantBits());
        this.origin.writeLong(uniqueId.getLeastSignificantBits());
        return this;
    }

    public UUID readUniqueId() {
        return new UUID(this.origin.readLong(), this.origin.readLong());
    }

    public CodecBuffer writeInt(int value) {
        this.origin.writeInt(value);
        return this;
    }

    public int readInt() {
        return this.origin.readInt();
    }

    public CodecBuffer writeEnum(Enum<?> value) {
        this.origin.writeInt(value.ordinal());
        return this;
    }

    public <T extends Enum<?>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[this.origin.readInt()];
    }

    public CodecBuffer writeObject(@Nullable Object object, Consumer<CodecBuffer> consumer) {
        this.writeBoolean(object != null);

        if (object != null) {
            consumer.accept(this);
        }

        return this;
    }

    public <T> @Nullable T readObject(Class<T> tClass, Supplier<T> supplier) {
        var notNull = this.readBoolean();

        if (notNull) {
            return supplier.get();
        } else {
            return null;
        }
    }

    public <T> void writeList(@NotNull List<T> list, BiConsumer<CodecBuffer, T> consumer) {
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

    public void writeBuffer(CodecBuffer buffer) {
        this.writeInt(buffer.getOrigin().readableBytes());
        this.writeBytes(buffer.getOrigin());
    }

    public CodecBuffer writeLong(long value) {
        this.origin.writeLong(value);
        return this;
    }

    public long readLong() {
        return this.origin.readLong();
    }

    public CodecBuffer writeFloat(float value) {
        this.origin.writeFloat(value);
        return this;
    }

    public float readFloat() {
        return this.origin.readFloat();
    }

    public CodecBuffer writeDouble(double value) {
        this.origin.writeDouble(value);
        return this;
    }

    public double readDouble() {
        return this.origin.readDouble();
    }

    public short readShort() {
        return this.origin.readShort();
    }

    public CodecBuffer writeShort(short value) {
        this.origin.writeShort(value);
        return this;
    }

    public CodecBuffer writeByte(byte value) {
        this.origin.writeByte(value);
        return this;
    }

    public byte readByte() {
        return this.origin.readByte();
    }

    public CodecBuffer writeBytes(Buffer bytes) {
        this.origin.writeBytes(bytes);
        return this;
    }

    public CodecBuffer writeBytes(byte[] bytes) {
        this.origin.writeBytes(bytes);
        return this;
    }
}