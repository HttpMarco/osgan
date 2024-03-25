package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.utils.executers.FutureResult;
import io.netty5.channel.Channel;
import io.netty5.util.concurrent.FutureListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class CommunicationComponent<M extends Metadata> {

    private final M metadata;
    private final FutureResult<Void> connectionFuture = new FutureResult<>();

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
}
