package dev.httpmarco.osgan.networking;

import io.netty5.channel.Channel;
import io.netty5.channel.EventLoopGroup;
import io.netty5.util.concurrent.FutureListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class CommunicationComponent extends CommunicationListener {

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private CommunicationFuture<Void> connectionFuture = new CommunicationFuture<>();
    @Getter(AccessLevel.PROTECTED)
    private final EventLoopGroup bossGroup;
    @Getter(AccessLevel.PROTECTED)
    private final String hostname;
    @Getter(AccessLevel.PROTECTED)
    private final int port;

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
}