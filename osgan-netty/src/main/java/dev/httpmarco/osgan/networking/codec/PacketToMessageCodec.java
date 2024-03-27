package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.annotation.PacketIgnore;
import dev.httpmarco.osgan.reflections.Reflections;
import io.netty5.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketToMessageCodec extends AbstractMessageToPacket {

    @Override
    public void encode(ChannelHandlerContext ctx, Packet msg, @NotNull CodecBuffer buffer) throws Exception {
        try {
            buffer.writeString(msg.getClass().getName());
            buffer.writeLong(System.currentTimeMillis());

            for (var field : msg.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(PacketIgnore.class)) {
                    continue;
                }
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    buffer.writeString(field.get(msg).toString());
                } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                    buffer.writeBoolean((Boolean) field.get(msg));
                } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                    buffer.writeLong((Long) field.get(msg));
                } else if (field.getType().equals(Short.class) || field.getType().equals(short.class)) {
                    buffer.writeShort((Short) field.get(msg));
                } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                    buffer.writeInt((Integer) field.get(msg));
                } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                    buffer.writeDouble((Double) field.get(msg));
                } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
                    buffer.writeFloat((Float) field.get(msg));
                } else if (field.getType().equals(Byte.class) || field.getType().equals(byte.class)) {
                    buffer.writeByte((Byte) field.get(msg));
                } else if (field.getType().equals(UUID.class)) {
                    buffer.writeUUID((UUID) field.get(msg));
                } else if (field.getType().isEnum()) {
                    buffer.writeEnum((Enum<?>) field.get(msg));
                } else {
                    throw new Exception("Unsupported type: " + field.getType().getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void decode(@NotNull ChannelHandlerContext ctx, @NotNull CodecBuffer buffer) throws Exception {
        try {
            var clazz = Class.forName(buffer.readString());
            var time = buffer.readLong();
            var packet = new Reflections<>(clazz).allocate();

            for (var field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(PacketIgnore.class)) {
                    continue;
                }
                field.setAccessible(true);

                if (field.getType().equals(String.class)) {
                    field.set(packet, buffer.readString());
                } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                    field.set(packet, buffer.readBoolean());
                } else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                    field.set(packet, buffer.readLong());
                } else if (field.getType().equals(Short.class) || field.getType().equals(short.class)) {
                    field.set(packet, buffer.readShort());
                } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                    field.set(packet, buffer.readInt());
                } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                    field.set(packet, buffer.readDouble());
                } else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
                    field.set(packet, buffer.readFloat());
                } else if (field.getType().equals(Byte.class) || field.getType().equals(byte.class)) {
                    field.set(packet, buffer.readByte());
                } else if (field.getType().equals(UUID.class)) {
                    field.set(packet, buffer.readUUID());
                } else if (field.getType().isEnum()) {
                    field.set(packet, buffer.readEnum((Class<? extends Enum<?>>) field.getType()));
                } else {
                    throw new Exception("Unsupported type: " + field.getType().getName());
                }
            }
            buffer.resetBuffer();
            ctx.fireChannelRead(packet);
            System.err.println("time: " + (System.currentTimeMillis() - time) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
