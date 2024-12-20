package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.Channel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class CommunicationClientTransmit extends ChannelTransmit {
    private final CommunicationComponent<?> communicationComponent;

    public CommunicationClientTransmit(CommunicationComponent<?> communicationComponent, Channel channel) {
        super(channel);
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

    @Override
    public <P extends Packet> void listen(Class<P> listeningClass, BiConsumer<ChannelTransmit, P> packetCallback) {
        communicationComponent.listen(listeningClass, packetCallback);
    }

    @Contract("_ -> new")
    public static @NotNull CommunicationClientTransmit empty(CommunicationComponent<?> communicationComponent) {
        return new CommunicationClientTransmit(communicationComponent, null);
    }

    public static @NotNull CommunicationClientTransmit of(CommunicationComponent<?> communicationComponent, @NotNull ChannelTransmit transmit) {
        var clientTransmit = new CommunicationClientTransmit(communicationComponent, transmit.channel());
        clientTransmit.listeners().putAll(transmit.listeners());
        return clientTransmit;
    }
}
