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
        this.sendPacket(this.channel, object);
    }

    public void sendPacket(Channel channel, Packet object) {
        this.writeAndFlush(channel, object);
    }

    public void redirectPacket(String id, Packet object) {
        //this.sendPacket(new ForwardPacket(id, object));
    }

    @SneakyThrows
    private void writeAndFlush(Channel channel, Packet packet) {
        channel.writeAndFlush(packet);
    }
}