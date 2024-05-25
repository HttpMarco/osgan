package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.server.NettyServer;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicLong;

public class ServerTest {

    @Test
    public void handle() throws InterruptedException {
        var server = NettyServer.builder().build();

        AtomicLong time = new AtomicLong(System.currentTimeMillis());

        server.listen(AuthPacket.class, (channel, packet) -> {
            System.out.println((System.currentTimeMillis() - time.get()) + " ms");
        });

        server.registerResponder("groups-all", (transmit, properties) -> {
            return new AuthPacket("a", 4432);
        });

        var client = NettyClient.builder()
                .withConnectTimeout(500)
                .build();

        client.connect();
        Thread.sleep(1000);
        time.set(System.currentTimeMillis());

        client.sendPacket(new AuthPacket("a", 43));

        /*
        client.request("groups-all", AuthPacket.class, authPacket -> {
            System.out.println("response");
            System.out.println((System.currentTimeMillis() - time.get()) + " ms");
        });

         */

        Thread.currentThread().join();
    }
}