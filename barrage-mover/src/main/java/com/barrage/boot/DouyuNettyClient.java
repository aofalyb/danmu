package com.barrage.boot;

import com.barrage.transport.ConnClientChannelHandler;
import com.barrage.transport.codec.PacketDecoder;
import com.barrage.transport.codec.PacketEncoder;
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

public class DouyuNettyClient implements NettyClient{



    private String rid = null;

    public DouyuNettyClient(String rid) {
        this.rid = rid;
    }

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private void createClient(Listener listener, EventLoopGroup workerGroup, ChannelFactory<? extends Channel> channelFactory) {
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
        listener.onSuccess();
    }

    public ChannelFuture connect(String host, int port) {
        return bootstrap.connect(new InetSocketAddress(host, port));
    }
    public ChannelFuture connect(String host, int port, Listener listener) {
        return bootstrap.connect(new InetSocketAddress(host, port)).addListener(f -> {
            if (f.isSuccess()) {
                if (listener != null) listener.onSuccess(port);

            } else {
                if (listener != null) listener.onFailure(f.cause());

            }
        });
    }
    private void createNioClient(Listener listener) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(
                getWorkThreadNum(), new DefaultThreadFactory("netty-tcp-client"), getSelectorProvider()
        );
        workerGroup.setIoRatio(getIoRate());
        createClient(listener, workerGroup, getChannelFactory());
    }
    private void createEpollClient(Listener listener) {
        EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(
                getWorkThreadNum(), new DefaultThreadFactory("netty-tcp-client")
        );
        workerGroup.setIoRatio(getIoRate());
        createClient(listener, workerGroup, EpollSocketChannel::new);
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
        return 10;
    }

    protected int getWorkThreadNum() {
        return 1;
    }

    public ChannelHandler getChannelHandler(){
        return new ConnClientChannelHandler(rid);
    }


    protected void doStart(Listener listener) throws Throwable {
        if (useNettyEpoll()) {
            createEpollClient(listener);
        } else {
            createNioClient(listener);
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


    protected void doStop(Listener listener) throws Throwable {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        listener.onSuccess();
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

    public interface Listener {
        void onSuccess(Object... args);

        void onFailure(Throwable cause);
    }
}
