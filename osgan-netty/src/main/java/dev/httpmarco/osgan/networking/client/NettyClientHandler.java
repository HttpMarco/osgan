package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.channel.SimpleChannelInboundHandler;

public final class NettyClientHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Packet packet) {

    }
}
