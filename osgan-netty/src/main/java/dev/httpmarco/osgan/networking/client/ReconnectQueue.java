package dev.httpmarco.osgan.networking.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class ReconnectQueue extends Thread {

    private static final long RECONNECT_TIMEOUT = 5000;
    private final NettyClient nettyClient;

    @Override
    @SneakyThrows
    public void run() {
        while ((Thread.currentThread().isAlive())) {
            Thread.sleep(RECONNECT_TIMEOUT);

            this.nettyClient.connect();
        }
    }
}
