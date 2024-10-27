package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.*;
import dev.httpmarco.osgan.networking.packet.*;
import dev.httpmarco.osgan.networking.request.RequestClient;
import dev.httpmarco.osgan.networking.security.SecurityChannelParametrize;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.epoll.Epoll;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(fluent = true)
public final class CommunicationClient extends RequestClient {

    private final Bootstrap bootstrap;

    @Getter
    private CommunicationClientTransmit channelTransmit;

    public CommunicationClient(String hostname, int port) {
        this(null, hostname, port);
    }

    public CommunicationClient(SecurityChannelParametrize securityChannelParametrize, String hostname, int port) {
        super(0, hostname, port);

        // default listener transmit
        this.channelTransmit = CommunicationClientTransmit.empty(this);

        this.bootstrap = new Bootstrap()
                .group(bossGroup())
                .channelFactory(CommunicationNetworkUtils::createChannelFactory)
                .handler(new CommunicationClientChannelInitializer(securityChannelParametrize, new CommunicationTransmitHandler(
                        this,
                        (it) -> List.of(channelTransmit),
                        this::call,
                        (it) -> {
                            channelTransmit = CommunicationClientTransmit.of(this, it);
                            callClientAction(CommunicationClientAction.CONNECTED);
                        },
                        (it) -> callClientAction(CommunicationClientAction.DISCONNECTED))
                ))
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.IP_TOS, 24)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);

        if (Epoll.isTcpFastOpenClientSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }
    }

    @Override
    public void initialize() {
        this.bootstrap.connect(hostname(), port()).addListener(future -> {
            if (future.isSuccess()) {
                this.connectionFuture().complete(null);
                return;
            }
            // todo add reason to listener
            callClientAction(CommunicationClientAction.FAILED);
            this.connectionFuture(null);
        });
    }

    @Override
    public void close() {
        callClientAction(CommunicationClientAction.CLIENT_DISCONNECT);
        super.close();
    }

    @Override
    public void sendPacket(Packet packet) {
        this.channelTransmit.sendPacket(packet);
    }

    @Override
    protected void callClientAction(CommunicationClientAction action) {
        this.callClientAction(action, channelTransmit);
    }
}