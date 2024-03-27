package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.listening.ChannelPacketListener;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.channel.SimpleChannelInboundHandler;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public final class CommunicationComponentHandler extends SimpleChannelInboundHandler<Packet> {

    private ChannelConsumer onActive, onInactive;
    private ChannelPacketListener<? extends Packet> onPacketReceived;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Packet packet) {
        this.onPacketReceived.listenWithMapping(new ChannelTransmit(ctx.channel()), packet);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {
        this.supplyChannelTransmit(ctx.channel(), this.onActive);
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {
        this.supplyChannelTransmit(ctx.channel(), this.onInactive);
    }

    private void supplyChannelTransmit(Channel channel, ChannelConsumer consumer) {
        if (consumer != null) {
            consumer.listen(new ChannelTransmit(channel));
        }
    }
}
