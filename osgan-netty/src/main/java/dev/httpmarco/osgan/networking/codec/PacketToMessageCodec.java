package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.annotation.PacketIgnore;
import dev.httpmarco.osgan.networking.annotation.PacketIncludeObject;
import dev.httpmarco.osgan.reflections.Reflections;
import io.netty5.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketToMessageCodec extends AbstractMessageToPacket {

    @Override
    public void encode(ChannelHandlerContext ctx, Packet msg, @NotNull CodecBuffer buffer) throws Exception {
        try {
            buffer.writeLong(System.currentTimeMillis());
            encodeObject(buffer, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void encodeObject(@NotNull CodecBuffer buffer, @NotNull Object packet) throws Exception {
        buffer.writeString(packet.getClass().getName());

        for (var field : packet.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PacketIgnore.class)) {
                continue;
            }
            field.setAccessible(true);
            if (encodeParameter(buffer, field.get(packet))) {
                continue;
            }

            if (field.isAnnotationPresent(PacketIncludeObject.class) || field.getType().isAnnotationPresent(PacketIncludeObject.class)) {
                encodeObject(buffer, field.get(packet));
            } else if (field.getType().isArray()) {
                var array = (Object[]) field.get(packet);
                buffer.writeInt(array.length);
                for (var obj : array) {
                    if (this.encodeParameter(buffer, obj)) {
                        continue;
                    }
                    encodeObject(buffer, obj);
                }
            } else {
                System.err.println("Encode - Unsupported type: " + field.getType().getName() + " in packet " + packet.getClass().getName());
            }
        }
    }

    private boolean encodeParameter(CodecBuffer buffer, Object parameter) {
        var type = parameter.getClass();
        if (type.equals(String.class)) {
            buffer.writeString(parameter.toString());
            return true;
        } else if (type.equals(Boolean.class)) {
            buffer.writeBoolean((Boolean) parameter);
            return true;
        } else if (type.equals(Long.class)) {
            buffer.writeLong((Long) parameter);
            return true;
        } else if (type.equals(Short.class)) {
            buffer.writeShort((Short) parameter);
            return true;
        } else if (type.equals(Integer.class)) {
            buffer.writeInt((Integer) parameter);
            return true;
        } else if (type.equals(Double.class)) {
            buffer.writeDouble((Double) parameter);
            return true;
        } else if (type.equals(Float.class)) {
            buffer.writeFloat((Float) parameter);
            return true;
        } else if (type.equals(Byte.class)) {
            buffer.writeByte((Byte) parameter);
            return true;
        } else if (type.equals(UUID.class)) {
            buffer.writeUUID((UUID) parameter);
            return true;
        } else if (type.isEnum()) {
            buffer.writeEnum((Enum<?>) parameter);
            return true;
        }
        return false;
    }

    @Override
    public void decode(@NotNull ChannelHandlerContext ctx, @NotNull CodecBuffer buffer) throws Exception {
        try {
            var time = buffer.readLong();
            var packet = this.decodeObject(buffer);
            buffer.resetBuffer();
            ctx.fireChannelRead(packet);
            System.err.println("time: " + (System.currentTimeMillis() - time) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Object decodeObject(@NotNull CodecBuffer buffer) throws ClassNotFoundException, IllegalAccessException {
        var clazz = Class.forName(buffer.readString());
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
            } else if (field.isAnnotationPresent(PacketIncludeObject.class) || field.getType().isAnnotationPresent(PacketIncludeObject.class)) {
                field.set(packet, decodeObject(buffer));
            } else {
                System.err.println("Decode - Unsupported type: " + field.getType().getName() + " in packet " + clazz.getName());
            }
        }
        return packet;
    }

}
