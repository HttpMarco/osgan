package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommunicationServerTransmit extends ChannelTransmit {
    private final CommunicationComponent<CommunicationServerAction> communicationComponent;

    public CommunicationServerTransmit(Channel channel, CommunicationComponent<CommunicationServerAction> communicationComponent) {
        super(channel);
        this.communicationComponent = communicationComponent;
    }

    //TODO check this
    @Override
    public <P extends Packet> boolean call(@NotNull P packet, ChannelTransmit transmit) {
        return this.communicationComponent.call(packet, transmit);
    }

    @Override
    public <P extends Packet> void listen(Class<P> listeningClass, Consumer<P> packetCallback) {
        communicationComponent.listen(listeningClass, packetCallback);
    }

    public static CommunicationServerTransmit of(ChannelTransmit channelTransmit, CommunicationComponent<CommunicationServerAction> communicationComponent) {
        var transmit = new CommunicationServerTransmit(channelTransmit.channel(), communicationComponent);
        transmit.listeners().putAll(transmit.listeners());
        return transmit;
    }
}
