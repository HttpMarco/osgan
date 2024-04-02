package dev.httpmarco.osgan.networking.client;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public final class ReconnectQueue extends Thread {

    private static final long RECONNECT_TIMEOUT = 5000;
    private final NettyClient nettyClient;

    @Override
    @SneakyThrows
    public void run() {
        while (this.isAlive()) {
            if (!this.nettyClient.isConnected()) {
                this.nettyClient.connect();
            } else {
                interrupt();
            }
            //TODO

            Thread.sleep(RECONNECT_TIMEOUT);
        }
    }
}