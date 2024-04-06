package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.files.json.JsonUtils;
import dev.httpmarco.osgan.networking.*;
import dev.httpmarco.osgan.networking.packet.ChannelTransmitAuthPacket;
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
                            if (metadata.id() != null) {
                                it.sendPacket(new ChannelTransmitAuthPacket(metadata().id()));
                            }
                            this.transmit = it;
                        })
                        .onInactive(it -> {
                            if ((metadata.hasReconnection())) {
                                System.out.println("Starting reconnect queue...");
                                this.reconnectQueue.start();
                            }

                            this.transmit = null;
                        })
                        .onPacketReceived(this::callPacketReceived)
                        .build()))
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.IP_TOS, 24)
                //.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, metadata().connectionTimeout());

        if (Epoll.isTcpFastOpenClientSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }

        this.listen(ForwardPacket.class, (transmit, packet) -> {
            if (packet.id().equals(metadata.id())) {
                try {
                    this.callPacketReceived(transmit, (Packet) JsonUtils.fromJson(packet.packetJson(), Class.forName(packet.className())));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.listen(RequestPacket.class, (transmit, packet) -> {
            if (this.requestHandler().isResponderPresent(packet.id())) {
                this.sendPacket(new ResponsePacket(packet.uniqueId(), JsonUtils.toJson(this.requestHandler().getResponder(packet.id()).response(transmit, packet.properties()))));
            }
        });
        this.listen(BadResponsePacket.class, (transmit, packet) -> {
            if (this.requestHandler().isRequestPresent(packet.uniqueId())) {
                this.requestHandler().removeRequest(packet.uniqueId());

                System.out.println("Received bad response for request '" + packet.uniqueId() + "': " + packet.message());
            }
        });
        this.listen(ResponsePacket.class, (transmit, packet) -> {
            if (this.requestHandler().isRequestPresent(packet.uniqueId())) {
                this.requestHandler().acceptRequest(packet.uniqueId(), packet.packetJson());
            }
        });

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
    public boolean isServer() {
        return false;
    }
}