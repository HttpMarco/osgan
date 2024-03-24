package dev.httpmarco.osgan.networking.utils;

import io.netty5.channel.EventLoop;
import io.netty5.channel.MultithreadEventLoopGroup;
import io.netty5.channel.epoll.Epoll;
import io.netty5.channel.epoll.EpollHandler;
import io.netty5.channel.epoll.EpollSocketChannel;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.channel.socket.nio.NioSocketChannel;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class NetworkUtils {

    @Contract("_ -> new")
    public static @NotNull MultithreadEventLoopGroup createEventLoopGroup(int threads) {
        return new MultithreadEventLoopGroup(threads, Epoll.isAvailable() ? EpollHandler.newFactory() : NioHandler.newFactory());
    }

    public static @NotNull SocketChannel createChannelFactory(EventLoop loop) {
        return Epoll.isAvailable() ? new EpollSocketChannel(loop) : new NioSocketChannel(loop);
    }
}
