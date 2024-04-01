package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.codec.BufferDecoder;
import dev.httpmarco.osgan.networking.codec.BufferEncoder;
import dev.httpmarco.osgan.networking.codec.PacketDecoder;
import dev.httpmarco.osgan.networking.codec.PacketEncoder;
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
        channel.pipeline().addLast(new BufferDecoder())
                .addLast(new PacketDecoder())
                .addLast(new BufferEncoder())
                .addLast(new PacketEncoder())
                .addLast(communicationComponentHandler);
    }
}
