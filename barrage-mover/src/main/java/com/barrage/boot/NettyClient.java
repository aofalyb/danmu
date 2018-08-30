package com.barrage.boot;

import com.barrage.netty.Listener;
import com.barrage.netty.NettyClientException;
import com.barrage.netty.codec.PacketDecoder;
import com.barrage.netty.codec.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * netty client的定位是一个引导启动/停止的客户端，而{@link com.barrage.netty.Connection}
 * 的定位是维持C/S的长连接。
 */
public abstract class NettyClient {


    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;


    private void createClient(EventLoopGroup workerGroup, ChannelFactory<? extends Channel> channelFactory) {
        this.workerGroup = workerGroup;
        this.bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channelFactory(channelFactory);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                initPipeline(ch.pipeline());
            }
        });
        initOptions(bootstrap);
    }

    protected ChannelFuture connect(String host, int port) {
        return bootstrap.connect(new InetSocketAddress(host, port));
    }
    protected ChannelFuture connect(String host, int port, Listener listener) {
        return bootstrap.connect(new InetSocketAddress(host, port)).addListener(f -> {
            if (f.isSuccess()) {
                if (listener != null) listener.onSuccess(port);

            } else {
                if (listener != null) listener.onFailure(f.cause());

            }
        });
    }
    private void createNioClient() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(
                getWorkThreadNum(), new DefaultThreadFactory("netty-tcp-client"), getSelectorProvider()
        );
        workerGroup.setIoRatio(getIoRate());
        createClient(workerGroup, getChannelFactory());
    }
    private void createEpollClient() {
        EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(
                getWorkThreadNum(), new DefaultThreadFactory("netty-tcp-client")
        );
        workerGroup.setIoRatio(getIoRate());
        createClient(workerGroup, EpollSocketChannel::new);
    }

    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("decoder", getDecoder());
        pipeline.addLast("encoder", getEncoder());
        pipeline.addLast("handler", getChannelHandler());
    }

    protected ChannelHandler getDecoder() {
        return new PacketDecoder();
    }

    protected ChannelHandler getEncoder() {
        return new PacketEncoder();
    }

    protected int getIoRate() {
        return 2;
    }

    protected int getWorkThreadNum() {
        return 1;
    }

    public abstract ChannelHandler getChannelHandler();

    /**
     * sync login
     * @throws NettyClientException
     */
    public abstract CountDownLatch login() throws NettyClientException;


    protected void init() throws Throwable {
        if (useNettyEpoll()) {
            createEpollClient();
        } else {
            createNioClient();
        }
    }

    private boolean useNettyEpoll() {
        if (false) {
            try {
                Native.offsetofEpollData();
                return true;
            } catch (UnsatisfiedLinkError error) {

            }
        }
        return false;
    }


    public void doStop() throws Throwable {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    protected void initOptions(Bootstrap b) {
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000 * 10);
        b.option(ChannelOption.TCP_NODELAY, true);
    }

    public ChannelFactory<? extends Channel> getChannelFactory() {
        return NioSocketChannel::new;
    }

    public SelectorProvider getSelectorProvider() {
        return SelectorProvider.provider();
    }

    @Override
    public String toString() {
        return "NettyClient{" +
                ", name=" + this.getClass().getSimpleName() +
                '}';
    }
}
