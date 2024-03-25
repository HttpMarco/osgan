package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.utils.executers.FutureResult;
import io.netty5.channel.Channel;
import io.netty5.channel.EventLoopGroup;
import io.netty5.util.concurrent.FutureListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public abstract class CommunicationComponent<M extends Metadata> {

    private final M metadata;
    private final EventLoopGroup bossGroup;

    @Setter
    private FutureResult<Void> connectionFuture = new FutureResult<>();

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
}
