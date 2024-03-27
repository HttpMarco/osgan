package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.ChannelInitializer;
import dev.httpmarco.osgan.networking.CommunicationComponentHandler;
import dev.httpmarco.osgan.networking.NetworkUtils;
import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.epoll.Epoll;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class NettyServer extends CommunicationComponent<ServerMetadata> {

    public NettyServer(ServerMetadata metadata) {
        super(metadata, 1);

        var bootstrap = new ServerBootstrap()
                .group(bossGroup(), NetworkUtils.createEventLoopGroup(0))
                .channelFactory(NetworkUtils.generateChannelFactory())
                .childHandler(new ChannelInitializer(new CommunicationComponentHandler()))
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

}
