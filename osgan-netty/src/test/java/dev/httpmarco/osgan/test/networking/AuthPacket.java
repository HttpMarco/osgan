package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class AuthPacket extends Packet {
    private final String test;
    private final int test1;
}
