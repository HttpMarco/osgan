package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.security.SecurityController;
import io.netty5.channel.Channel;
import io.netty5.channel.EventLoopGroup;
import io.netty5.util.concurrent.FutureListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Accessors(fluent = true)
public abstract class CommunicationComponent<E extends Enum<?>> extends CommunicationListener {

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private CommunicationFuture<Void> connectionFuture = new CommunicationFuture<>();
    @Getter(AccessLevel.PROTECTED)
    private final EventLoopGroup bossGroup;
    @Getter(AccessLevel.PROTECTED)
    private final String hostname;
    @Getter(AccessLevel.PROTECTED)
    private final int port;
    private final Map<E, List<Consumer<ChannelTransmit>>> localActions = new HashMap<>();

    public CommunicationComponent(int bossGroupThreads, String hostname, int port) {
        this.bossGroup = CommunicationNetworkUtils.createEventLoopGroup(bossGroupThreads);
        this.hostname = hostname;
        this.port = port;
    }

    public abstract void initialize();

    public FutureListener<? super Channel> handleConnectionRelease() {
        return it -> {
            if (it.isSuccess()) {
                connectionFuture.complete(null);
                it.getNow().closeFuture();
            } else {
                connectionFuture.completeExceptionally(it.cause());
            }
        };
    }

    public boolean isConnected() {
        return connectionFuture.isDone();
    }

    public boolean isAlive() {
        return !bossGroup.isShutdown() && !bossGroup.isTerminated() && !bossGroup.isShuttingDown();
    }

    public void close() {
        bossGroup.shutdownGracefully();
    }


    public void clientAction(E action, Consumer<ChannelTransmit> runnable) {
        var currentActionCollection = this.localActions.getOrDefault(action, new ArrayList<>());
        currentActionCollection.add(runnable);
        this.localActions.put(action, currentActionCollection);
    }

    protected void callClientAction(E action, @Nullable ChannelTransmit transmit) {
        if (this.localActions.containsKey(action)) {
            for (var runnable : localActions.get(action)) {
                runnable.accept(transmit);
            }
        }
    }

    protected void callClientAction(E action) {
        this.callClientAction(action, null);
    }
}