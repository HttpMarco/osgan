package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.CommunicationNetworkUtils;
import dev.httpmarco.osgan.networking.CommunicationTransmitHandler;
import dev.httpmarco.osgan.networking.channel.ChannelInitializer;
import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.bootstrap.Bootstrap;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.epoll.Epoll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class CommunicationClient extends CommunicationComponent {

    private final Bootstrap bootstrap;
    private CommunicationClientTransmit channelTransmit;

    // only custom client events
    private final Map<CommunicationClientAction, List<Consumer<ChannelTransmit>>> localClientActions = new HashMap<>();

    public CommunicationClient(String hostname, int port) {
        super(0, hostname, port);

        // default listener transmit
        this.channelTransmit = CommunicationClientTransmit.empty(this);

        this.bootstrap = new Bootstrap()
                .group(bossGroup())
                .channelFactory(CommunicationNetworkUtils::createChannelFactory)
                .handler(new ChannelInitializer(new CommunicationTransmitHandler(
                        (it) -> List.of(channelTransmit),
                        (it, channel) -> this.channelTransmit.call(it, channel),
                        (it) -> {
                            channelTransmit = CommunicationClientTransmit.of(this, it);
                            callClientAction(CommunicationClientAction.CONNECTED);
                        },
                        (it) -> callClientAction(CommunicationClientAction.DISCONNECTED))
                ))
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.IP_TOS, 24)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);

        if (Epoll.isTcpFastOpenClientSideAvailable()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }
    }

    @Override
    public void initialize() {
        this.bootstrap.connect(hostname(), port()).addListener(future -> {
            if (future.isSuccess()) {
                this.connectionFuture().complete(null);
                return;
            }
            // todo add reason to listener
            callClientAction(CommunicationClientAction.FAILED);
            this.connectionFuture(null);
        });
    }

    @Override
    public void close() {
        callClientAction(CommunicationClientAction.CLIENT_DISCONNECT);
        super.close();
    }

    @Override
    public void sendPacket(Packet packet) {
        this.channelTransmit.sendPacket(packet);
    }

    public CommunicationClient clientAction(CommunicationClientAction action, Consumer<ChannelTransmit> runnable) {
        var currentActionCollection = this.localClientActions.getOrDefault(action, new ArrayList<>());
        currentActionCollection.add(runnable);
        this.localClientActions.put(action, currentActionCollection);
        return this;
    }

    private void callClientAction(CommunicationClientAction action) {
        if (this.localClientActions.containsKey(action)) {
            for (var runnable : this.localClientActions.get(action)) {
                runnable.accept(this.channelTransmit);
            }
        }
    }
}