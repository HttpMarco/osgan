package dev.httpmarco.osgan.networking.security;

import io.netty5.channel.Channel;

public interface SecurityChannelParametrize {

    void channelAuthorize(Channel channel);

}
