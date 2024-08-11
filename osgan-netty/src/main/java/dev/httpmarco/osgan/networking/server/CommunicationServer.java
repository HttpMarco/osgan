package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.CommunicationNetworkUtils;
import dev.httpmarco.osgan.networking.CommunicationTransmitHandler;
import dev.httpmarco.osgan.networking.channel.ChannelInitializer;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.EventLoopGroup;
import lombok.Getter;
import lombok.experimental.Accessors;
import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public final class CommunicationServer extends CommunicationComponent<CommunicationServerAction> {

    @Getter
    private final List<ChannelTransmit> channels = new ArrayList<>();
    private final EventLoopGroup workerGroup = CommunicationNetworkUtils.createEventLoopGroup(0);

    public CommunicationServer(String hostname, int port) {
        super(1, hostname, port);
    }

    @Override
    public void initialize() {
        var bootstrap = new ServerBootstrap()
                .group(bossGroup(), workerGroup)
                .channelFactory(CommunicationNetworkUtils.generateChannelFactory())
                .childHandler(new ChannelInitializer(new CommunicationTransmitHandler(
                        (it) -> this.channels,
                        (it, channel) -> channel.call(it, channel),
                        (it) -> {
                            callClientAction(CommunicationServerAction.CLIENT_CONNECT, it);
                            channels.add(CommunicationServerTransmit.of(it, this));
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
}
