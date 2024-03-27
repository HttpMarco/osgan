package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.annotation.PacketIncludeObject;

public class AuthPacket implements Packet {

    private VerifyPlayer player;

    public AuthPacket(VerifyPlayer player) {
        this.player = player;
    }
}
