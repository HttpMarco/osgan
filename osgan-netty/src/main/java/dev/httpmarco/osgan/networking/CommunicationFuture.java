package dev.httpmarco.osgan.networking;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@Accessors(fluent = true)
public final class CommunicationFuture<E> extends CompletableFuture<E> {

    public CommunicationFuture() {
        this.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    public E sync(E defaultValue, long secondTimeout) {
        try {
            return get(secondTimeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public E sync(E defaultValue) {
        return sync(defaultValue, 5);
    }
}
