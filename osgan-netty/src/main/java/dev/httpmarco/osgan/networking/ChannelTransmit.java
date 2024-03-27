package dev.httpmarco.osgan.networking;

import io.netty5.channel.Channel;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "id")
public final class ChannelTransmit {

    @Setter
    private String id = "unknown";
    private final Channel channel;

    public <P extends Packet> void sendPacket(P object) {
        channel.writeAndFlush(object);
    }
}