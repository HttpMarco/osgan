package dev.httpmarco.osgan.test.networking;

import dev.httpmarco.osgan.networking.Packet;
import lombok.Getter;
import lombok.experimental.Accessors;

public class AuthPacket implements Packet {

    private VerifyPlayer player;

    public AuthPacket(VerifyPlayer player) {
        this.player = player;
    }

    public VerifyPlayer getPlayer() {
        return player;
    }
}
