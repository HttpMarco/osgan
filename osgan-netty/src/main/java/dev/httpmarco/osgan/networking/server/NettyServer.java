package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.*;
import dev.httpmarco.osgan.networking.packet.ChannelTransmitAuthPacket;
import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.EventLoopGroup;
import io.netty5.channel.epoll.Epoll;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class NettyServer extends CommunicationComponent<ServerMetadata> {

    private final EventLoopGroup workerGroup = NetworkUtils.createEventLoopGroup(0);

    @Getter
    @Accessors(fluent = true)
    private final List<ChannelTransmit> transmits = new ArrayList<>();

    public NettyServer(ServerMetadata metadata) {
        super(metadata, 1);
        var bootstrap = new ServerBootstrap()
                .group(bossGroup(), workerGroup)
                .channelFactory(NetworkUtils.generateChannelFactory())
                .childHandler(new ChannelInitializer(CommunicationComponentHandler
                        .builder()
                        .onActive(this.transmits::add)
                        .onInactive(transmits::remove)
                        .onPacketReceived((channel, packet) -> {
                            if (packet instanceof ChannelTransmitAuthPacket authPacket) {
                                transmits.stream().filter(it -> it.channel().equals(channel.channel())).findFirst().ifPresent(transmit -> transmit.id(authPacket.id()));
                                return;
                            }
                            callPacketReceived(channel, packet);
                        })
                        .build()))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        if (Epoll.isTcpFastOpenServerSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN, 3);
        }
        bootstrap.bind(metadata().hostname(), metadata().port()).addListener(handleConnectionRelease());
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull NettyServerBuilder builder() {
        return new NettyServerBuilder();
    }

    @Override
    public void close() {
        super.close();
        workerGroup.shutdownGracefully();
    }
}
