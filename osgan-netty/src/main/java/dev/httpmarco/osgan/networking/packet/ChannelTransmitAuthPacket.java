package dev.httpmarco.osgan.networking.packet;

import dev.httpmarco.osgan.networking.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class ChannelTransmitAuthPacket implements Packet {

    private String id;

}
