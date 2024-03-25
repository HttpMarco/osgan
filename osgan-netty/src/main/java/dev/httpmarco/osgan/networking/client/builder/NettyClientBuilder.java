package dev.httpmarco.osgan.networking.client.builder;

import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.client.metadata.NettyClientMeta;
import dev.httpmarco.osgan.utils.types.ListUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NettyClientBuilder {

    private int port = 9090;
    private int connectTimeout = 5000;
    private long reconnectSchedule = -1;
    private Map<NettyClient.Event, List<Runnable>> listeners = new HashMap<NettyClient.Event, List<Runnable>>();

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

    public NettyClientBuilder listen(NettyClient.Event event, Runnable runnable) {
        this.listeners.put(event, ListUtils.append(listeners.getOrDefault(event, new LinkedList<>()), runnable));
        return this;
    }

    public NettyClient createAndStart() {
        return new NettyClient(new NettyClientMeta("0.0.0.0", port, reconnectSchedule, connectTimeout, listeners));
    }
}
