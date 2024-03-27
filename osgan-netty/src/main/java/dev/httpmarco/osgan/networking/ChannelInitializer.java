package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.codec.PacketToMessageCodec;
import io.netty5.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Setter
@Accessors(fluent = true)
@AllArgsConstructor
public final class ChannelInitializer extends io.netty5.channel.ChannelInitializer<Channel> {

    private final CommunicationComponentHandler communicationComponentHandler;

    @Override
    protected void initChannel(@NotNull Channel channel) {
        channel.pipeline().addLast(new PacketToMessageCodec(), communicationComponentHandler);
    }
}
