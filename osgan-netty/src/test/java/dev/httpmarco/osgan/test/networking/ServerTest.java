package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.server.NettyServer;
import org.junit.jupiter.api.Test;

public class ServerTest {

    @Test
    public void handle() {
        var server = NettyServer.builder().build();

        while (server.isAlive()) {
        }
    }
}