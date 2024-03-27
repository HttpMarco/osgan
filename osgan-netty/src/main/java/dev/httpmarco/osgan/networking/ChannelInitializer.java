package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.codec.PacketToMessageCodec;
import io.netty5.channel.Channel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public final class ChannelInitializer extends io.netty5.channel.ChannelInitializer<Channel> {

    private final CommunicationComponentHandler communicationComponentHandler;

    @Override
    protected void initChannel(@NotNull Channel channel) {
        channel.pipeline().addLast(new PacketToMessageCodec(), communicationComponentHandler);
    }
}
