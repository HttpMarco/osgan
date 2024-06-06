package dev.httpmarco.osgan.networking.packet;

public abstract class Packet {

    public abstract void read(PacketBuffer buffer);

    public abstract void write(PacketBuffer buffer);

}
