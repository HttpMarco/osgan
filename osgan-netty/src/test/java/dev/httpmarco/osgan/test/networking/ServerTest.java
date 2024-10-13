package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.client.CommunicationClient;
import dev.httpmarco.osgan.networking.client.CommunicationClientAction;
import dev.httpmarco.osgan.networking.server.CommunicationServer;
import dev.httpmarco.osgan.networking.server.CommunicationServerAction;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServerTest {

    @Test
    @SneakyThrows
    public void handle() {
        var server = new CommunicationServer("127.0.0.1", 8080);

        var client1 = new CommunicationClient("127.0.0.1", 8080);
        var client2 = new CommunicationClient("127.0.0.1", 8080);

        client1.clientAction(CommunicationClientAction.CONNECTED, (it) -> {
            System.out.println("client1 action - CONNECTED");
        });
        client1.clientAction(CommunicationClientAction.DISCONNECTED, (it) -> {
            System.out.println("client1 action - DISCONNECTED");
        });
        client1.clientAction(CommunicationClientAction.FAILED, (it) -> {
            System.out.println("client1 action - FAILED");
        });

        client2.clientAction(CommunicationClientAction.CONNECTED, (it) -> {
            System.out.println("client2 action - CONNECTED");
        });
        client2.clientAction(CommunicationClientAction.DISCONNECTED, (it) -> {
            System.out.println("client2 action - DISCONNECTED");
        });
        client2.clientAction(CommunicationClientAction.FAILED, (it) -> {
            System.out.println("client2 action - FAILED");
        });


        server.clientAction(CommunicationServerAction.CLIENT_CONNECT, transmit -> {
            System.out.println("connected " + transmit.channel().remoteAddress());
        });

        server.clientAction(CommunicationServerAction.CLIENT_DISCONNECT, transmit -> {
            System.out.println("disconnected " + transmit.channel().remoteAddress());
        });


        server.initialize();

        client1.initialize();
        client2.initialize();


        Thread.sleep(200);

        server.registerResponder("test", property -> new TestPacket("polo", property.getInteger("test")));

        client1.registerResponder("players", property -> new TestPacket("polo", property.getInteger("test")));

        Thread.sleep(1000);

        TestPacket request1 = client1.request("players", TestPacket.class, new CommunicationProperty().set("test", 200));
        Assertions.assertEquals(new TestPacket("polo", 200), request1);

        Thread.sleep(1000);

        TestPacket request2 = client2.request("players", TestPacket.class, new CommunicationProperty().set("test", 300));
        Assertions.assertEquals(new TestPacket("polo", 300), request2);

        Thread.sleep(1000);

        TestPacket request3 = server.request("players", TestPacket.class, new CommunicationProperty().set("test", 400));
        Assertions.assertEquals(new TestPacket("polo", 400), request3);

        Thread.sleep(1000);

        TestPacket request4 = client1.request("test", TestPacket.class, new CommunicationProperty().set("test", 500));
        Assertions.assertEquals(new TestPacket("polo", 500), request4);

        Thread.sleep(1000);

        TestPacket request5 = server.request("test", TestPacket.class, new CommunicationProperty().set("test", 600));
        Assertions.assertEquals(new TestPacket("polo", 600), request5);

        Thread.sleep(1000);

        client1.requestAsync("test1", TestPacket.class, new CommunicationProperty().set("test", 700)).thenAccept(testPacket -> {
            System.out.println(testPacket == null);
        });

        Thread.sleep(1000);

        server.requestAsync("test1", TestPacket.class, new CommunicationProperty().set("test", 800)).thenAccept(testPacket -> {
            System.out.println(testPacket == null);
        });

        Thread.sleep(1000);

        client1.close();
        client2.close();
        server.close();
    }
}