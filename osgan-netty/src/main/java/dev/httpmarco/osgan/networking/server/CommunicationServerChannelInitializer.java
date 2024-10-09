package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.CommunicationTransmitHandler;
import dev.httpmarco.osgan.networking.channel.ChannelInitializer;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import io.netty5.channel.Channel;
import org.jetbrains.annotations.NotNull;

public final class CommunicationServerChannelInitializer extends ChannelInitializer {

    private final CommunicationServer server;

    public CommunicationServerChannelInitializer(CommunicationServer server, CommunicationTransmitHandler transmitHandler) {
        super(transmitHandler);
        this.server = server;
    }

    @Override
    protected void initChannel(@NotNull Channel channel) {
        var securityController = server.securityController();

        // if no security controller is set, proceed as usual
        if (securityController == null) {
            super.initChannel(channel);
            return;
        }

        var transmit = new ChannelTransmit(channel);
        if (securityController.handleFirstConnection(transmit)) {
            securityController.authorize(transmit);
            super.initChannel(channel);
        } else {
            securityController.unauthorize(transmit);
        }
    }
}
