package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.channel.ChannelShutdownDirection;
import io.netty5.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
public final class CommunicationTransmitHandler extends SimpleChannelInboundHandler<Packet> {

    private final Function<Channel, List<ChannelTransmit>> findTransmitFunction;
    private final BiConsumer<Packet, ChannelTransmit> channelTransmitPacketConsumer;
    private final Consumer<ChannelTransmit> channelActvieConsumer;
    private final Consumer<ChannelTransmit> channelInactiveConsumer;

    @Override
    protected void messageReceived(@NotNull ChannelHandlerContext channelHandlerContext, Packet packet) {
        channelTransmitPacketConsumer.accept(packet, findTransmitFunction.apply(channelHandlerContext.channel()).stream().filter(it -> it.channel().equals(channelHandlerContext.channel())).findFirst().orElseThrow());
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext ctx) {
        this.channelActvieConsumer.accept(new ChannelTransmit(ctx.channel().id().asLongText(), ctx.channel()));
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) {
        this.channelInactiveConsumer.accept(new ChannelTransmit(ctx.channel().id().asLongText(), ctx.channel()));
    }

    @Override
    public void channelExceptionCaught(ChannelHandlerContext ctx, @NotNull Throwable cause) {
        if (!cause.getMessage().equals("Connection reset")) cause.printStackTrace();
    }
}
