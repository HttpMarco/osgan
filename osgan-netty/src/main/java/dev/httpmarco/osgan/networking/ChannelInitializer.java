package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.codec.ByteToPacketCodec;
import io.netty5.channel.Channel;
import org.jetbrains.annotations.NotNull;

public final class ChannelInitializer extends io.netty5.channel.ChannelInitializer<Channel> {

    @Override
    protected void initChannel(@NotNull Channel channel) throws Exception {
        channel.pipeline().addLast(new ByteToPacketCodec());
    }
}
