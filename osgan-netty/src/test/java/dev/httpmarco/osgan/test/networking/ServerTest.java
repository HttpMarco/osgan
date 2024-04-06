package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.files.json.JsonUtils;
import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.server.NettyServer;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class ServerTest {

    @Test
    public void handle() throws InterruptedException {
        System.out.println(JsonUtils.fromJson(JsonUtils.toJson(new AuthPacket()), AuthPacket.class));

//        var client = NettyClient.builder()
//                .withId("polo")
//                .withHostname("127.0.0.1")
//                .withConnectTimeout(500)
//                .build();
//
//
//        Thread.sleep(1000);
//
//        client.sendPacket(new AuthPacket());
//
//        Thread.sleep(10000);
    }
}