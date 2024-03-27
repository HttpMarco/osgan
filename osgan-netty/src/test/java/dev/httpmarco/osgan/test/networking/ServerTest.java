package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.server.NettyServer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServerTest {

    @Test
    public void handle() throws InterruptedException {
        var server = NettyServer.builder().build();

        var client = NettyClient.builder()
                .withHostname("127.0.0.1")
                .withConnectTimeout(500)
                .build();


        Thread.sleep(1000);

        client.sendPacket(new AuthPacket(new VerifyPlayer(UUID.randomUUID(), "Marco", "1234", "1234", true, "Marco", "1234")));

        Thread.sleep(10000);
    }
}