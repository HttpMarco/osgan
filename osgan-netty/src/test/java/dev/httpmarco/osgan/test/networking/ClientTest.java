package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.client.NettyClient;
import org.junit.jupiter.api.Test;

public class ClientTest {

    @Test
    public void handle() {
        NettyClient.builder()
                .withPort(80)
                .withConnectTimeout(500)
                .createAndStart();
        assert true;
    }
}
