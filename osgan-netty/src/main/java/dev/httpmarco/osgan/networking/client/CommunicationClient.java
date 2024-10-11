package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.*;
import dev.httpmarco.osgan.networking.channel.ChannelInitializer;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.*;
import dev.httpmarco.osgan.networking.security.SecurityChannelParametrize;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.epoll.Epoll;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Accessors(fluent = true)
public final class CommunicationClient extends CommunicationComponent<CommunicationClientAction> {

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
                        (it, channel) -> this.channelTransmit.call(it, channel),
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
    public void requestReceive(ChannelTransmit channelTransmit, RequestPacket packet) {
        if (!hasResponder(packet.id())) {
            return;
        }

        respond(channelTransmit, packet);
    }

    @Override
    public void badRequestReceive(ChannelTransmit channelTransmit, BadRequestPacket packet) {
        requests().remove(packet.uuid());
        System.out.println("Invalid request (" + packet.uuid() + "): " + packet.message());
    }

    @Override
    public void responseReceive(ChannelTransmit channelTransmit, RequestResponsePacket packet) {
        if (!requests().containsKey(packet.uuid())) {
            return;
        }

        ((CommunicationFuture<Packet>) requests().get(packet.uuid())).complete(packet.buildPacket());
        requests().remove(packet.uuid());
    }

    @Override
    protected void callClientAction(CommunicationClientAction action) {
        this.callClientAction(action, channelTransmit);
    }

    @Override
    public void responder(String id, Function<CommunicationProperty, Packet> packetFunction) {
        super.responder(id, packetFunction);

        this.sendPacket(new RegisterResponderPacket(id));
    }
}