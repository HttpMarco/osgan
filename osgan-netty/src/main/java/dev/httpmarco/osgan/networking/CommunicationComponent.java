package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.listening.ChannelPacketListener;
import dev.httpmarco.osgan.utils.executers.FutureResult;
import io.netty5.channel.Channel;
import io.netty5.channel.EventLoopGroup;
import io.netty5.util.concurrent.FutureListener;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public abstract class CommunicationComponent<M extends Metadata> {

    @Setter
    private FutureResult<Void> connectionFuture = new FutureResult<>();

    private final M metadata;
    private final EventLoopGroup bossGroup;
    private final Map<ChannelTransmit, Map<Class<?>, List<ChannelPacketListener>>> packetListeners = new HashMap<>();

    public CommunicationComponent(M metadata, int workerThreads) {
        this.bossGroup = NetworkUtils.createEventLoopGroup(workerThreads);
        this.metadata = metadata;
    }

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

    public void callPacketReceived(ChannelTransmit transmit, Packet packet) {

    }

}
