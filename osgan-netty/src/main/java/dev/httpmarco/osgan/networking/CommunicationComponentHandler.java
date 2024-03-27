package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.channel.SimpleChannelInboundHandler;

import java.util.function.Consumer;

public class CommunicationComponentHandler extends SimpleChannelInboundHandler<Packet> {

    private Consumer<Channel> channelActiveConsumer;
    private Consumer<Channel> channelInactiveConsumer;

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Packet packet) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.channelActiveConsumer.accept(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.channelInactiveConsumer.accept(ctx.channel());
    }
}
