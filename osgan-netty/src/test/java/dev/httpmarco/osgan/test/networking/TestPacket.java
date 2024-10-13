package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.UUID;

@Getter
@ToString
@Accessors(fluent = true)
@AllArgsConstructor
public class TestPacket extends Packet {

    private String name;
    private int test;

    @Override
    public void read(PacketBuffer buffer) {
        this.name = buffer.readString();
        this.test = buffer.readInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeString(name);
        buffer.writeInt(test);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestPacket packet) {
            return packet.name.equals(name) && packet.test == test();
        }

        return false;
    }
}
