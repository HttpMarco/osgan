package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.CommunicationFuture;
import dev.httpmarco.osgan.networking.CommunicationNetworkUtils;
import dev.httpmarco.osgan.networking.CommunicationTransmitHandler;
import dev.httpmarco.osgan.networking.channel.ChannelInitializer;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.*;
import dev.httpmarco.osgan.networking.security.SecurityController;
import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Accessors(fluent = true)
public final class CommunicationServer extends CommunicationComponent<CommunicationServerAction> {

    private static final Random RANDOM = new Random();

    @Getter
    private final List<ChannelTransmit> channels = new ArrayList<>();
    private final EventLoopGroup workerGroup = CommunicationNetworkUtils.createEventLoopGroup(0);

    @Nullable
    @Getter(AccessLevel.PROTECTED)
    private SecurityController securityController;

    public CommunicationServer(String hostname, int port) {
        super(1, hostname, port);
    }

    @Override
    public void initialize() {
        var bootstrap = new ServerBootstrap()
                .group(bossGroup(), workerGroup)
                .channelFactory(CommunicationNetworkUtils.generateChannelFactory())
                .childHandler(new CommunicationServerChannelInitializer(this, new CommunicationTransmitHandler(
                        this,
                        (it) -> this.channels,
                        (it, channel) -> channel.call(it, channel),
                        (it) -> {
                            channels.add(CommunicationServerTransmit.of(it, this));
                            callClientAction(CommunicationServerAction.CLIENT_CONNECT, it);
                        },
                        (it) -> {
                            callClientAction(CommunicationServerAction.CLIENT_DISCONNECT, it);
                            channels.removeIf(channel -> channel.channel().equals(it.channel()));
                        }
                )))

                // all channel options
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.bind(hostname(), port()).addListener(handleConnectionRelease())
                .addListener(future -> {
                    if (!future.isSuccess()) {
                        throw new RuntimeException(future.cause());
                    }
                });

        clientAction(CommunicationServerAction.CLIENT_DISCONNECT, transmit -> {
            if (!respondersByChannel().containsKey(transmit)) {
                return;
            }

            respondersByChannel().get(transmit).forEach(s -> {
                servicesWithResponders().get(s).remove(transmit);

                if (servicesWithResponders().get(s).isEmpty()) {
                    System.out.println("Unregistered responder: " + s);
                }
            });

            respondersByChannel().remove(transmit);
        });
    }

    public CommunicationServer useSecurityRules(SecurityController securityController) {
        this.securityController = securityController;
        return this;
    }

    @Override
    public void close() {
        workerGroup.shutdownGracefully();
        super.close();
    }

    @Override
    public void sendPacket(Packet packet) {
        this.channels.forEach(channelTransmit -> channelTransmit.sendPacket(packet));
    }

    @Override
    public void requestReceive(ChannelTransmit channelTransmit, RequestPacket packet) {
        if (hasResponder(packet.id())) {
            this.respond(channelTransmit, packet);
            return;
        }

        if (servicesWithResponders().containsKey(packet.id())) {
            pending().put(packet.uuid(), new PendingRequest(
                    channelTransmit,
                    packet.id(),
                    packet.uuid(),
                    System.currentTimeMillis()
            ));

            var responders = servicesWithResponders().get(packet.id());
            var responder = responders.get(RANDOM.nextInt(responders.size()));

            responder.sendPacket(packet);

            System.out.println("Received request '" + packet.uuid() + "' for '" + packet.id() + "'");
            return;
        }

        channelTransmit.sendPacket(new BadRequestPacket(packet.uuid(), "No responder registered!"));
    }

    @Override
    public void badRequestReceive(ChannelTransmit channelTransmit, BadRequestPacket packet) {
        if (!pending().containsKey(packet.uuid())) {
            return;
        }

        pending().get(packet.uuid()).transmit().sendPacket(packet);
    }

    @Override
    public void responseReceive(ChannelTransmit channelTransmit, RequestResponsePacket packet) {
        if (!pending().containsKey(packet.uuid())) {
            return;
        }

        pending().get(packet.uuid()).transmit().sendPacket(packet);
    }
}
