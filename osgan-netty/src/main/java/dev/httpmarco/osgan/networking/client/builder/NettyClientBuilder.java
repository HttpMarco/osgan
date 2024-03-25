package dev.httpmarco.osgan.networking.client.builder;

import dev.httpmarco.osgan.networking.AbstractCommunicationComponentBuilder;
import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.client.ClientMetadata;

import java.util.concurrent.TimeUnit;

public class NettyClientBuilder extends AbstractCommunicationComponentBuilder<NettyClient, NettyClientBuilder> {

    private int connectTimeout = 5000;
    private long reconnectSchedule = -1;


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
        return new NettyClient(new ClientMetadata(hostname(), port(), reconnectSchedule, connectTimeout));
    }
}
