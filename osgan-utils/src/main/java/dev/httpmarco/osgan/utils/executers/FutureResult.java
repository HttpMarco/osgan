package dev.httpmarco.osgan.utils.executers;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
@Accessors(fluent = true)
public class FutureResult<E> extends CompletableFuture<E> {

    public E sync(E defaultValue, long secondTimeout) {
        try {
            return get(secondTimeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}