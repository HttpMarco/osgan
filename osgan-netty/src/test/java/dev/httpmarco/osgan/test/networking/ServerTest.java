package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.server.NettyServer;
import org.junit.jupiter.api.Test;

public class ServerTest {

    @Test
    public void handle() throws InterruptedException {
        var server = NettyServer.builder().build();

        var client = NettyClient.builder()
                .withHostname("127.0.0.1")
                .withConnectTimeout(500)
                .build();


        Thread.sleep(1000);

        client.sendPacket(new TestPacket());

        //client.close();


        Thread.sleep(10000);
    }
}