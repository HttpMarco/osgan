package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.client.NettyClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class ClientTest {

    @Test
    public void handle() {
        var client = NettyClient.builder()
                .withPort(80)
                .withConnectTimeout(500)
                //.withReconnect(TimeUnit.MINUTES, 2)
                .build();

        while (client.isAlive()) {
        }
    }
}
