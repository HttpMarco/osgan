package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.*;
import dev.httpmarco.osgan.networking.packet.ChannelTransmitAuthPacket;
import dev.httpmarco.osgan.utils.executers.FutureResult;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.epoll.Epoll;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true)
public final class NettyClient extends CommunicationComponent<ClientMetadata> {

    private final Bootstrap bootstrap;
    private @Nullable ChannelTransmit transmit;
    private final ReconnectQueue reconnectQueue = new ReconnectQueue(this);

    public NettyClient(ClientMetadata metadata) {
        super(metadata, 0);

        this.bootstrap = new Bootstrap()
                .group(bossGroup())
                .channelFactory(NetworkUtils::createChannelFactory)
                .handler(new ChannelInitializer(CommunicationComponentHandler
                        .builder()
                        .onActive(it -> {
                            if (metadata.id() == null) {
                                it.sendPacket(new ChannelTransmitAuthPacket(metadata().id()));
                            }
                            this.transmit = it;
                        })
                        .onInactive(it -> {
                            if ((metadata.hasReconnection())) {
                                this.reconnectQueue.start();
                            }
                            this.transmit = null;
                        })
                        .build()))
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

    public <P extends Packet> void sendPacket(P packet) {
        if (this.transmit != null) {
            this.transmit.sendPacket(packet);
        }
    }
}