package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.utils.NetworkUtils;
import dev.httpmarco.osgan.utils.executers.FutureResult;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.EventLoopGroup;
import io.netty5.channel.epoll.Epoll;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class NettyClient {

    private FutureResult<Void> connectionFuture;
    private final EventLoopGroup eventLoopGroup = NetworkUtils.createEventLoopGroup(0);

    @Contract(value = " -> new", pure = true)
    public static @NotNull NettyClientBuilder builder() {
        return new NettyClientBuilder();
    }

    public NettyClient(String hostname, int port, int connectTimeout) {
        this.connectionFuture = new FutureResult<>();
        var bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channelFactory(NetworkUtils::createChannelFactory)
                .handler(new NettyClientHandler());

        bootstrap.option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);

        if (Epoll.isTcpFastOpenClientSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }
        bootstrap.connect(hostname, port).addListener(future -> {
            if (future.isSuccess()) {
                connectionFuture.complete(null);
                return;
            }
            connectionFuture.fail(future.cause());
        });
    }
}