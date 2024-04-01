package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.packet.ForwardPacket;
import io.netty5.channel.Channel;
import io.netty5.util.concurrent.Future;
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
        this.sendPacket(new ForwardPacket(id, object));
    }

    @SneakyThrows
    private Future<Void> writeAndFlush(Channel channel, Packet packet) {
        packet.getBuffer().getOrigin().readerOffset(0);

        var future = channel.writeAndFlush(packet);
        return future.asStage().sync().future();
    }
}