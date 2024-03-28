package dev.httpmarco.osgan.networking.listening;

import dev.httpmarco.osgan.networking.ChannelTransmit;
import dev.httpmarco.osgan.networking.Packet;

@FunctionalInterface
public interface ChannelPacketListener<P> {

    void listen(ChannelTransmit channel, P packet);

    @SuppressWarnings("unchecked")
    default void listenWithMapping(ChannelTransmit transmit, Packet packet) {
        listen(transmit, (P) packet);
    }

}
