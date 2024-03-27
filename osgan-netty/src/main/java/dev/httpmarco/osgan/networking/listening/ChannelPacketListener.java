package dev.httpmarco.osgan.networking.listening;

import dev.httpmarco.osgan.networking.ChannelTransmit;
import dev.httpmarco.osgan.networking.Packet;

@FunctionalInterface
public interface ChannelPacketListener {

    void listen(ChannelTransmit channel, Packet packet);

}
