package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.files.json.JsonUtils;
import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.server.NettyServer;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ServerTest {

    @Test
    public void handle() throws InterruptedException {
       // System.out.println(JsonUtils.fromJson(JsonUtils.toJson(new AuthPacket()), AuthPacket.class));

      //

        var client = NettyClient.builder()
                .withHostname("127.0.0.1")
                .withConnectTimeout(500)
                .withReconnect(TimeUnit.SECONDS, 5)
                .build();


       Thread.sleep(11000);

        var server = NettyServer.builder().build();

        Thread.sleep(11000);

     //   client.sendPacket(new AuthPacket());

    //    Thread.sleep(1000);

        server.close();


        Thread.sleep(11000);

        server = NettyServer.builder().build();

        Thread.sleep(11000);
    }
}