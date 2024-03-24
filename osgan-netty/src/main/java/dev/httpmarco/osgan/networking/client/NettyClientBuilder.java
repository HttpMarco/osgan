package dev.httpmarco.osgan.networking.client;

import java.util.concurrent.TimeUnit;

public class NettyClientBuilder {

    private int port = 9090;
    private int connectTimeout = 5000;
    private long reconnectSchedule = -1;

    public NettyClientBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public NettyClientBuilder withConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public NettyClientBuilder withReconnect(TimeUnit timeUnit, long time) {
        this.reconnectSchedule = timeUnit.toMillis(time);
        return this;
    }

    public NettyClient createAndStart() {
        return new NettyClient("0.0.0.0", port, connectTimeout);
    }
}
