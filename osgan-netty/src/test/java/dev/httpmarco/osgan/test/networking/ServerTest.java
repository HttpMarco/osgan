package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.client.CommunicationClient;
import dev.httpmarco.osgan.networking.server.CommunicationServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServerTest {

    @Test
    @SneakyThrows
    public void handle() {

        var server = new CommunicationServer("127.0.0.1", 8080);
        var client = new CommunicationClient("127.0.0.1", 8080);

        server.initialize();
        client.initialize();

        Thread.sleep(200);

        client.sendPacket(new testpacket("test", UUID.randomUUID(), System.currentTimeMillis()));

        server.responder("players", property -> new testpacket("polo", UUID.randomUUID(), System.currentTimeMillis()));

        client.request("players", new CommunicationProperty(), testpacket.class, testpacket -> {
            System.out.println("request work");
        });

        server.channels().get(0).sendPacket(new testpacket("test2", UUID.randomUUID(), System.currentTimeMillis()));
        server.sendPacket(new testpacket("test3", UUID.randomUUID(), System.currentTimeMillis()));

        Thread.currentThread().join();
    }
}