package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.files.json.JsonObjectSerializer;
import dev.httpmarco.osgan.files.json.JsonUtils;
import dev.httpmarco.osgan.networking.*;
import dev.httpmarco.osgan.networking.packet.ForwardPacket;
import dev.httpmarco.osgan.networking.request.packets.BadResponsePacket;
import dev.httpmarco.osgan.networking.request.packets.RequestPacket;
import dev.httpmarco.osgan.networking.request.packets.ResponsePacket;
import dev.httpmarco.osgan.utils.executers.FutureResult;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.Channel;
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
                            this.transmit = it;
                            this.reconnectQueue.pauseThread();

                            metadata.onActive().listen(it);
                        })
                        .onInactive(it -> {
                            this.transmit = null;

                            metadata.onInactive().listen(it);

                            if (metadata.hasReconnection()) {
                                this.reconnectQueue.resumeThread();
                            }
                        })
                        .onPacketReceived(this::callPacketReceived)
                        .build()))
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.IP_TOS, 24)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, metadata().connectionTimeout());

        if (Epoll.isTcpFastOpenClientSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }

        this.listen(RequestPacket.class, (transmit, packet) -> {
            if (this.requestHandler().isResponderPresent(packet.id())) {
                this.sendPacket(new ResponsePacket(packet.uniqueId(), this.requestHandler().getResponder(packet.id()).response(transmit, new JsonObjectSerializer(packet.properties()))));
            }
        });
        this.listen(BadResponsePacket.class, (transmit, packet) -> {
            if (this.requestHandler().isRequestPresent(packet.uniqueId())) {
                this.requestHandler().removeRequest(packet.uniqueId());
            }
        });
        this.listen(ResponsePacket.class, (transmit, packet) -> {
            if (this.requestHandler().isRequestPresent(packet.uniqueId())) {
                this.requestHandler().acceptRequest(packet.uniqueId(), packet.packet());
            }
        });

        this.reconnectQueue.start();
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
                this.connectionFuture().complete(null);
                return;
            }
            if (metadata().hasReconnection()) {
                return;
            }
            this.connectionFuture().completeExceptionally(future.cause());
            this.connectionFuture(null);
        });
    }

    @Override
    public <P extends Packet> void sendPacket(P packet) {
        if (this.transmit != null) {
            this.transmit.sendPacket(packet);
        }
    }

    @Override
    public <P extends Packet> void sendPacket(Channel channel, P packet) {
        if (this.transmit != null) {
            this.transmit.sendPacket(channel, packet);
        }
    }

    @Override
    public <P extends Packet> void redirectPacket(String id, P packet) {
        if (this.transmit != null) {
            this.transmit.redirectPacket(id, packet);
        }
    }

    @Override
    public boolean isConnected() {
        return transmit != null;
    }
}