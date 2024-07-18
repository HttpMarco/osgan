package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.client.CommunicationClient;
import dev.httpmarco.osgan.networking.client.CommunicationClientAction;
import dev.httpmarco.osgan.networking.server.CommunicationServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServerTest {

    @Test
    @SneakyThrows
    public void handle() {

        var server = new CommunicationServer("127.0.0.1", 8080);

        var client = new CommunicationClient("127.0.0.1", 8081);


        client.clientAction(CommunicationClientAction.CONNECTED, (it) -> {
            System.out.println("client action - CONNECTED");
        });
        client.clientAction(CommunicationClientAction.DISCONNECTED, (it) -> {
            System.out.println("client action - DISCONNECTED");
        });
        client.clientAction(CommunicationClientAction.CLIENT_DISCONNECT, (it) -> {
            System.out.println("client action - CLIENT_DISCONNECT");
        });
        client.clientAction(CommunicationClientAction.FAILED, (it) -> {
            System.out.println("client action - FAILED");
        });


        server.initialize();
        client.initialize();


        Thread.sleep(200);


        server.responder("players", property -> {
            System.out.println(property.getInteger("test"));
            return new testpacket("polo", UUID.randomUUID(), System.currentTimeMillis());
        });

        client.request("players", new CommunicationProperty().set("test", 200), testpacket.class, testpacket -> {
            System.out.println("request work");
        });

        // server.channels().get(0).sendPacket(new testpacket("test2", UUID.randomUUID(), System.currentTimeMillis()));
        //server.sendPacket(new testpacket("test3", UUID.randomUUID(), System.currentTimeMillis()));

        Thread.sleep(2000);

        client.close();
        server.close();

        Thread.sleep(2000);

    }
}