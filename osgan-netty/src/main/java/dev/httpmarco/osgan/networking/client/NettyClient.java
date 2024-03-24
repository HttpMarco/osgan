package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.utils.NetworkUtils;
import dev.httpmarco.osgan.utils.executers.FutureResult;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.EventLoopGroup;
import io.netty5.channel.epoll.Epoll;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@Accessors(fluent = true)
public final class NettyClient {

    private final NettyClientMeta meta;
    private final Bootstrap bootstrap;

    private FutureResult<Void> connectionFuture;
    private final EventLoopGroup eventLoopGroup = NetworkUtils.createEventLoopGroup(0);

    @Contract(value = " -> new", pure = true)
    public static @NotNull NettyClientBuilder builder() {
        return new NettyClientBuilder();
    }

    public NettyClient(NettyClientMeta meta) {
        this.meta = meta;
        this.bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channelFactory(NetworkUtils::createChannelFactory)
                .handler(new NettyClientHandler());

        bootstrap.option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, meta.connectionTimeout());

        if (Epoll.isTcpFastOpenClientSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }

        this.connect();
        new ReconnectQueue(this).start();
    }

    public void connect() {
        this.connectionFuture = new FutureResult<>();
        bootstrap.connect(meta.hostname(), meta.port()).addListener(future -> {
            if (future.isSuccess()) {
                connectionFuture.complete(null);
                return;
            }
            connectionFuture.completeExceptionally(future.cause());
        });
    }
}