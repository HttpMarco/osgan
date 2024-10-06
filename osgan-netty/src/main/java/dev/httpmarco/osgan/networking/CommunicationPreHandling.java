package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;

public interface CommunicationPreHandling {

    boolean allowAccess(ChannelTransmit transmit, Packet packet);

}
