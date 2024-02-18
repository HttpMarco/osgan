package dev.httpmarco.osgan.utils.executers;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Setter @Getter
@Accessors(fluent = true)
public class ThreadAsyncExecutor<E>{

    private final CompletableFuture<E> completableFuture;
    private final long timeout = 5;

    @SuppressWarnings("unchecked")
    public ThreadAsyncExecutor() {
        this.completableFuture = (CompletableFuture<E>) new CompletableFuture<>().exceptionally(throwable -> {
            throw new RuntimeException(throwable);
        });
    }

    public static <E> ThreadAsyncExecutor<E> of(CompletableFuture<E> future) {
        var executor = new ThreadAsyncExecutor<E>();
        future.whenComplete((response, throwable) -> {
            if(throwable != null) {
                throw new RuntimeException(throwable);
            }
            executor.complete(response);
        });
        return executor;
    }

    public void complete(E response) {
        completableFuture.complete(response);
    }

    public E sync(E defaultValue) {
        try {
            return completableFuture.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}