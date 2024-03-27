package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.annotation.PacketIgnore;
import dev.httpmarco.osgan.networking.annotation.PacketIncludeObject;
import dev.httpmarco.osgan.reflections.Reflections;
import io.netty5.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketToMessageCodec extends AbstractMessageToPacket {

    @Override
    public void encode(ChannelHandlerContext ctx, Packet msg, @NotNull CodecBuffer buffer) {
        try {
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
            var type = field.getType();
            if (field.isAnnotationPresent(PacketIncludeObject.class) || type.isAnnotationPresent(PacketIncludeObject.class)) {
                encodeObject(buffer, field.get(packet));
            } else if (type.isArray()) {
                var array = (Object[]) field.get(packet);
                buffer.writeInt(array.length);
                for (var object : array) {
                    if (this.encodeParameter(buffer, object)) {
                        continue;
                    }
                    encodeObject(buffer, object);
                }
            } else if (Collection.class.isAssignableFrom(type)) {
                var collection = (Collection<?>) field.get(packet);
                buffer.writeString(collection.getClass().getName());
                buffer.writeInt(collection.size());
                for (var object : collection) {
                    if (this.encodeParameter(buffer, object)) {
                        continue;
                    }
                    encodeObject(buffer, object);
                }
            } else {
                System.err.println("Encode - Unsupported type: " + type.getName() + " in packet " + packet.getClass().getName());
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
    public void decode(@NotNull ChannelHandlerContext ctx, @NotNull CodecBuffer buffer) {
        try {
            var packet = this.decodeObject(buffer);
            buffer.resetBuffer();
            ctx.fireChannelRead(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object decodeObject(@NotNull CodecBuffer buffer) throws ClassNotFoundException, IllegalAccessException {
        var clazz = Class.forName(buffer.readString());
        var packet = new Reflections<>(clazz).allocate();

        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PacketIgnore.class)) {
                continue;
            }
            field.setAccessible(true);

            var type = field.getType();
            var decodeParameter = decodeParameter(buffer, type);
            if (decodeParameter != null) {
                field.set(packet, decodeParameter);
                continue;
            }

            if (field.isAnnotationPresent(PacketIncludeObject.class) || type.isAnnotationPresent(PacketIncludeObject.class)) {
                field.set(packet, decodeObject(buffer));
            } else if (type.isArray()) {
                var array = (Object[]) Array.newInstance(type.getComponentType(), buffer.readInt());

                for (int i = 0; i < array.length; i++) {
                    var object = decodeParameter(buffer, array.getClass().getComponentType());
                    if (object != null) {
                        array[i] = object;
                        continue;
                    }
                    array[i] = decodeObject(buffer);
                }
                field.set(packet, array);
            } else if (Collection.class.isAssignableFrom(type)) {
                var collectionElementType = new Reflections<>(type).withField(field).generics()[0];
                var collectionType = Class.forName(buffer.readString());
                Collection<Object> collection;

                if (LinkedHashSet.class.isAssignableFrom(collectionType)) {
                    collection = new LinkedHashSet<>();
                } else if (Set.class.isAssignableFrom(collectionType)) {
                    collection = new HashSet<>();
                } else if (CopyOnWriteArrayList.class.isAssignableFrom(collectionType)) {
                    collection = new CopyOnWriteArrayList<>();
                } else if (LinkedList.class.isAssignableFrom(collectionType)) {
                    collection = new LinkedList<>();
                } else if (List.class.isAssignableFrom(collectionType)) {
                    collection = new ArrayList<>();
                } else {
                    System.err.println("Decode - Unsupported collection type: " + type.getName() + " in packet " + clazz.getName());
                    continue;
                }

                int size = buffer.readInt();

                for (var i = 0; i < size; i++) {
                    var object = decodeParameter(buffer, collectionElementType);
                    if (object != null) {
                        collection.add(object);
                        continue;
                    }
                    collection.add(decodeObject(buffer));
                }
                field.set(packet, collection);
            } else {
                System.err.println("Decode - Unsupported type: " + type.getName() + " in packet " + clazz.getName());
            }
        }
        return packet;
    }

    private @Nullable Object decodeParameter(CodecBuffer buffer, @NotNull Class<?> type) {
        if (type.equals(String.class)) {
            return buffer.readString();
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return buffer.readBoolean();
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return buffer.readLong();
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            return buffer.readShort();
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return buffer.readInt();
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return buffer.readDouble();
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return buffer.readFloat();
        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
            return buffer.readByte();
        } else if (type.equals(UUID.class)) {
            return buffer.readUUID();
        } else if (type.isEnum()) {
            return buffer.readEnum((Class<? extends Enum<?>>) type);
        }
        return null;
    }
}