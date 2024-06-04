package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.packet.PacketBuffer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunicationProperty {

    private final Map<String, Object> properties = new HashMap<>();


    public boolean has(String key) {
        return this.properties.containsKey(key);
    }

    public CommunicationProperty set(String key, int value) {
        this.properties.put(key, value);
        return this;
    }

    public CommunicationProperty set(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public CommunicationProperty set(String key, boolean value) {
        this.properties.put(key, value);
        return this;
    }

    public CommunicationProperty set(String key, long value) {
        this.properties.put(key, value);
        return this;
    }

    public CommunicationProperty set(String key, float value) {
        this.properties.put(key, value);
        return this;
    }

    public CommunicationProperty set(String key, short value) {
        this.properties.put(key, value);
        return this;
    }

    public CommunicationProperty set(String key, UUID value) {
        this.properties.put(key, value);
        return this;
    }

    public CommunicationProperty set(String key, Enum<?> value) {
        this.properties.put(key, value);
        return this;
    }

    public String getString(String key) {
        return (String) this.properties.get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) this.properties.get(key);
    }

    public Integer getInteger(String key) {
        return (Integer) this.properties.get(key);
    }

    public Float getFloat(String key) {
        return (Float) this.properties.get(key);
    }

    public Float getLong(String key) {
        return (Float) this.properties.get(key);
    }

    public Short getShort(String key) {
        return (Short) this.properties.get(key);
    }

    public UUID getUUID(String key) {
        return (UUID) this.properties.get(key);
    }

    public <T extends Enum<?>> T getEnum(String key, Class<T> enumClass) {
        return (T) this.properties.get(key);
    }


    public void write(@NotNull PacketBuffer buffer) {
        buffer.writeInt(properties.size());
        properties.forEach((s, o) -> {
            buffer.writeString(s);
            if (o instanceof Integer intValue) {
                buffer.writeEnum(PropertyTypes.INTEGER);
                buffer.writeInt(intValue);
            } else if (o instanceof String stringValue) {
                buffer.writeEnum(PropertyTypes.STRING);
                buffer.writeString(stringValue);
            } else if (o instanceof Float floatValue) {
                buffer.writeEnum(PropertyTypes.FLOAT);
                buffer.writeFloat(floatValue);
            } else if (o instanceof Short shortValue) {
                buffer.writeEnum(PropertyTypes.SHORT);
                buffer.writeShort(shortValue);
            } else if (o instanceof Enum<?> enumValue) {
                buffer.writeEnum(PropertyTypes.ENUM);
                buffer.writeString(enumValue.getClass().getName());
                buffer.writeEnum(enumValue);
            } else if (o instanceof UUID uuidValue) {
                buffer.writeEnum(PropertyTypes.UUID);
                buffer.writeUniqueId(uuidValue);
            } else if (o instanceof Boolean booleanValue) {
                buffer.writeEnum(PropertyTypes.BOOLEAN);
                buffer.writeBoolean(booleanValue);
            } else if (o instanceof Long longValue) {
                buffer.writeEnum(PropertyTypes.LONG);
                buffer.writeLong(longValue);
            } else {
                System.err.println("Unknown property: " + o.getClass().getSimpleName() + ":" + s);
            }
        });
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void read(@NotNull PacketBuffer buffer) {
        int size = buffer.readInt();

        for (int i = 0; i < size; i++) {
            var id = buffer.readString();
            var type = buffer.readEnum(PropertyTypes.class);

            properties.put(id, switch (type) {
                case ENUM -> buffer.readEnum(((Class<? extends Enum<?>>) Class.forName(buffer.readString())));
                case LONG -> buffer.readLong();
                case STRING -> buffer.readString();
                case SHORT -> buffer.readShort();
                case BOOLEAN -> buffer.readBoolean();
                case FLOAT -> buffer.readFloat();
                case UUID -> buffer.readUniqueId();
                case INTEGER -> buffer.readInt();
            });
        }
    }

    private enum PropertyTypes {
        INTEGER,
        STRING,
        LONG,
        FLOAT,
        SHORT,
        ENUM,
        UUID,
        BOOLEAN
    }
}
