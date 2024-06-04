package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.RequestPacket;
import io.netty5.channel.Channel;
import org.jetbrains.annotations.NotNull;

public class CommunicationServerTransmit extends ChannelTransmit {

    private final CommunicationComponent communicationComponent;

    public CommunicationServerTransmit(String id, Channel channel, CommunicationComponent communicationComponent) {
        super(id, channel);
        this.communicationComponent = communicationComponent;
    }

    @Override
    public void callResponder(ChannelTransmit channelTransmit, @NotNull RequestPacket requestPacket) {
        communicationComponent.callResponder(channelTransmit, requestPacket);
    }

    public static CommunicationServerTransmit of(ChannelTransmit channelTransmit, CommunicationComponent communicationComponent) {
        var transmit = new CommunicationServerTransmit(channelTransmit.id(), channelTransmit.channel(), communicationComponent);

        transmit.listeners().putAll(transmit.listeners());
        transmit.requests().putAll(transmit.requests());
        transmit.responders().putAll(transmit.responders());

        return transmit;
    }
}
