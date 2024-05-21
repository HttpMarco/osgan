package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.files.json.JsonUtils;
import dev.httpmarco.osgan.networking.*;
import dev.httpmarco.osgan.networking.packet.ForwardPacket;
import dev.httpmarco.osgan.networking.request.PendingRequest;
import dev.httpmarco.osgan.networking.request.packets.BadResponsePacket;
import dev.httpmarco.osgan.networking.request.packets.RegisterResponderPacket;
import dev.httpmarco.osgan.networking.request.packets.RequestPacket;
import dev.httpmarco.osgan.networking.request.packets.ResponsePacket;
import dev.httpmarco.osgan.utils.RandomUtils;
import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.EventLoopGroup;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class NettyServer extends CommunicationComponent<ServerMetadata> {

    private final EventLoopGroup workerGroup = NetworkUtils.createEventLoopGroup(0);

    @Getter
    @Accessors(fluent = true)
    private final List<ChannelTransmit> transmits = new ArrayList<>();

    private final Map<String, List<Channel>> responders = new HashMap<>();
    private final Map<Channel, List<String>> respondersByChannel = new HashMap<>();
    private final Map<UUID, PendingRequest> pending = new HashMap<>();

    public NettyServer(ServerMetadata metadata) {
        super(metadata, 1);
        var bootstrap = new ServerBootstrap()
                .group(bossGroup(), workerGroup)
                .channelFactory(NetworkUtils.generateChannelFactory())
                .childHandler(new ChannelInitializer(CommunicationComponentHandler
                        .builder()
                        .onActive(it -> {
                            this.transmits.add(it);
                            metadata.onActive().listen(it);
                        })
                        .onInactive(it -> {
                            transmits.remove(it);
                            this.unregisterChannel(it.channel());
                            metadata.onInactive().listen(it);
                        })
                        .onPacketReceived(this::callPacketReceived)
                        .build()))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.IP_TOS, 24)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        this.listen(RegisterResponderPacket.class, (transmit, packet) -> {
            if (!responders.containsKey(packet.id())) {
                this.responders.put(packet.id(), new ArrayList<>());
            }

            if (!respondersByChannel.containsKey(transmit.channel())) {
                this.respondersByChannel.put(transmit.channel(), new ArrayList<>());
            }

            this.responders.get(packet.id()).add(transmit.channel());
            this.respondersByChannel.get(transmit.channel()).add(packet.id());
            System.out.println("Registered responder: " + packet.id());
        });
        this.listen(RequestPacket.class, (transmit, packet) -> {
            if (this.requestHandler().isResponderPresent(packet.id())) {
                transmit.sendPacket(new ResponsePacket(
                        packet.uniqueId(),
                        JsonUtils.toJson(this.requestHandler().getResponder(packet.id()).response(transmit, packet.properties()))
                ));
            } else if (responders.containsKey(packet.id()) && !responders.get(packet.id()).isEmpty()) {
                this.pending.put(packet.uniqueId(), new PendingRequest(transmit, packet.id(), packet.uniqueId(), System.currentTimeMillis()));

                var responders = this.responders.get(packet.id());
                var rndm = RandomUtils.getRandomNumber(responders.size());

                this.sendPacket(responders.get(rndm), packet);

                System.out.println("Received request '" + packet.uniqueId() + "': id: " + packet.id() + " - properties: " + packet.properties());
            } else {
                var err = "No responder registered for id '" + packet.id() + "'";

                transmit.sendPacket(new BadResponsePacket(
                        packet.id(),
                        packet.uniqueId(),
                        err
                ));

                System.out.println(err);
            }
        });
        this.listen(ResponsePacket.class, (transmit, packet) -> {
            if (this.pending.containsKey(packet.uniqueId())) {
                this.pending.get(packet.uniqueId()).transmit().sendPacket(packet);
            }
        });

        bootstrap.bind(metadata().hostname(), metadata().port()).addListener(handleConnectionRelease())
                .addListener(future -> {
                    if (future.isSuccess()) {
                        System.out.println("Started netty server on port " + metadata.port() + "!");
                    } else {
                        new RuntimeException(future.cause());
                    }
                });
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

    @Override
    public <P extends Packet> void sendPacket(P packet) {
        this.transmits.forEach(transmit -> transmit.sendPacket(packet));
    }

    @Override
    public <P extends Packet> void sendPacket(Channel channel, P packet) {
        this.transmits.stream().filter(transmit -> transmit.channel().equals(channel)).forEach(transmit -> transmit.sendPacket(channel, packet));
    }

    public <P extends Packet> void sendPacketAndIgnoreSelf(Channel ignore, P packet) {
        this.transmits.stream().filter(transmit -> !transmit.channel().equals(ignore)).forEach(transmit -> transmit.sendPacket(packet));
    }

    @Override
    public <P extends Packet> void redirectPacket(String id, P packet) {
        this.transmits.forEach(transmit -> transmit.redirectPacket(id, packet));
    }

    private void unregisterChannel(Channel channel) {
        if (this.respondersByChannel.containsKey(channel)) {
            var responders = this.respondersByChannel.get(channel);
            for (var responder : responders) {
                this.responders.get(responder).remove(channel);
                System.out.println("Unregistered responder: " + responder);
            }
            this.respondersByChannel.remove(channel);
        }
    }
}
