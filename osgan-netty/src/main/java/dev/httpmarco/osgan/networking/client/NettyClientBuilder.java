package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.AbstractCommunicationComponentBuilder;

import java.util.concurrent.TimeUnit;

public final class NettyClientBuilder extends AbstractCommunicationComponentBuilder<NettyClient, NettyClientBuilder> {

    private String id;
    private int connectTimeout = 5000;
    private long reconnectSchedule = -1;

    public NettyClientBuilder withId(String id) {
        this.id = id;
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

    @Override
    public NettyClient build() {
        return new NettyClient(new ClientMetadata(id, hostname(), port(), reconnectSchedule, connectTimeout));
    }
}
