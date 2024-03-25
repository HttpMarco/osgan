package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.client.NettyClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class ClientTest {

    @Test
    public void handle() {
        NettyClient client = NettyClient.builder()
                .withPort(80)
                .withConnectTimeout(500)
                //.withReconnect(TimeUnit.MINUTES, 2)
                .listen(NettyClient.Event.CONNECT, () -> {

                }).listen(NettyClient.Event.DISCONNECT, () -> {

                }).listen(NettyClient.Event.TRY_RECONNECT, () -> {

                })
                .createAndStart();

        while (client.isAlive()) {
        }
    }
}
