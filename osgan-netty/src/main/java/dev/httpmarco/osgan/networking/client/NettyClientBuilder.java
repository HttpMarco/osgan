package dev.httpmarco.osgan.networking.client;

public class NettyClientBuilder {

    private int port = 9090;
    private int connectTimeout = 5000;

    public NettyClientBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public NettyClientBuilder withConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public NettyClient createAndStart() {
        return new NettyClient("0.0.0.0", port, connectTimeout);
    }
}
