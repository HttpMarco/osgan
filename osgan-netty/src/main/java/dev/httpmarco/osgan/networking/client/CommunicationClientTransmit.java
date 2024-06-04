package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class CommunicationClientTransmit extends ChannelTransmit {

    private final CommunicationComponent communicationComponent;

    public CommunicationClientTransmit(CommunicationComponent communicationComponent, String id, Channel channel) {
        super(id, channel);
        this.communicationComponent = communicationComponent;
    }

    @Override
    public <P extends Packet> boolean call(@NotNull P packet, ChannelTransmit transmit) {
        return this.communicationComponent.call(packet, transmit);
    }

    @Override
    public <P extends Packet> void listen(Class<P> listeningClass, Consumer<P> packetCallback) {
        communicationComponent.listen(listeningClass, packetCallback);
    }

    public static CommunicationClientTransmit empty(CommunicationComponent communicationComponent) {
        return new CommunicationClientTransmit(communicationComponent, null, null);
    }

    public static CommunicationClientTransmit of(CommunicationComponent communicationComponent, ChannelTransmit transmit) {
        var clientTransmit = new CommunicationClientTransmit(communicationComponent, transmit.id(), transmit.channel());
        clientTransmit.listeners().putAll(transmit.listeners());
        clientTransmit.requests().putAll(transmit.requests());
        clientTransmit.responders().putAll(transmit.responders());
        return clientTransmit;
    }
}
