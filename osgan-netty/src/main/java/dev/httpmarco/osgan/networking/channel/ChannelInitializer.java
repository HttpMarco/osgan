package dev.httpmarco.osgan.networking.channel;

import dev.httpmarco.osgan.networking.codec.PacketDecoder;
import dev.httpmarco.osgan.networking.codec.PacketEncoder;
import dev.httpmarco.osgan.networking.CommunicationTransmitHandler;
import io.netty5.channel.Channel;
import io.netty5.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty5.handler.codec.LengthFieldPrepender;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Setter
@Accessors(fluent = true)
@AllArgsConstructor
public final class ChannelInitializer extends io.netty5.channel.ChannelInitializer<Channel> {

    private final CommunicationTransmitHandler transmitHandler;

    @Override
    protected void initChannel(@NotNull Channel channel) {
        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, Integer.BYTES, 0, Integer.BYTES))
                .addLast(new PacketDecoder())
                .addLast(new LengthFieldPrepender(Integer.BYTES))
                .addLast(new PacketEncoder())
                .addLast(transmitHandler);
    }
}
