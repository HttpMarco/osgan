package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.files.json.JsonUtils;
import dev.httpmarco.osgan.networking.packet.ForwardPacket;
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

    public <P extends Packet> void sendPacket(Channel channel, P object) {
        channel.writeAndFlush(object);
    }

    public <P extends Packet> void redirectPacket(String id, P object) {
        this.sendPacket(new ForwardPacket(id, object.getClass().getName(), JsonUtils.toJson(object)));
    }
}