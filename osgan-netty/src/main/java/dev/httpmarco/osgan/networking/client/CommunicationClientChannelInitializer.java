package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.CommunicationTransmitHandler;
import dev.httpmarco.osgan.networking.channel.ChannelInitializer;
import dev.httpmarco.osgan.networking.security.SecurityChannelParametrize;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelHandlerContext;
import org.jetbrains.annotations.Nullable;

public final class CommunicationClientChannelInitializer extends ChannelInitializer {

    private final @Nullable SecurityChannelParametrize channelParametrize;

    public CommunicationClientChannelInitializer(@Nullable SecurityChannelParametrize channelParametrize, CommunicationTransmitHandler transmitHandler) {
        super(transmitHandler);

        this.channelParametrize = channelParametrize;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        if(channelParametrize != null) {
            channelParametrize.channelAuthorize(ctx.channel());
        }

        super.handlerRemoved(ctx);
    }
}
