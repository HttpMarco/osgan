package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.client.queue.ReconnectQueue;
import dev.httpmarco.osgan.networking.NetworkUtils;
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
public final class NettyClient extends CommunicationComponent<ClientMetadata> {

    private final Bootstrap bootstrap;
    private final ReconnectQueue reconnectQueue = new ReconnectQueue(this);

    public NettyClient(ClientMetadata metadata) {
        super(metadata, NetworkUtils.createEventLoopGroup(0));

        this.bootstrap = new Bootstrap()
                .group(bossGroup())
                .channelFactory(NetworkUtils::createChannelFactory)
                .handler(new NettyClientHandler())
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, metadata().connectionTimeout());

        if (Epoll.isTcpFastOpenClientSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }
        this.connect();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull NettyClientBuilder builder() {
        return new NettyClientBuilder();
    }

    public void connect() {
        this.connectionFuture(new FutureResult<>());

        this.bootstrap.connect(metadata().hostname(), metadata().port()).addListener(future -> {
            if (future.isSuccess()) {
                if (metadata().hasReconnection()) {
                    this.reconnectQueue.interrupt();
                }
                this.connectionFuture().complete(null);
                return;
            }
            if (metadata().hasReconnection()) {
                this.reconnectQueue.start();
            } else {
                this.connectionFuture().completeExceptionally(future.cause());
                this.connectionFuture(null);
            }
        });
    }
}