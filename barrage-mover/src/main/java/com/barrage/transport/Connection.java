package com.barrage.transport;

import com.barrage.message.DouyuHeartbeatMessage;
import com.barrage.protocol.Packet;
import com.barrage.util.DouyuPacketBuilder;
import com.barrage.common.Log;
import com.barrage.protocol.DouyuPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author liyang
 * @description: C/S连接
 * @date 2018/3/19
 */
public class Connection {

    private Channel channel;
    private volatile long lastReadTime = -1;
    private volatile long lastHeartBeatTime = -1;
    private static final long HEATBEAT_TIME_OUT = 40 * 1000;
    private static final long PEARID = 1 * 1000;
    private volatile boolean heatHeart = false;
    //练级所属房间
    private String rid;
    //维持心跳的线程，所有的实例共享。
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public Connection(Channel channel,String rid) {
        this.channel = channel;
        this.rid = rid;
    }


    public void refreshRead(){
        lastReadTime = System.currentTimeMillis();
    }

    private void refresHeartBeat(){
        lastHeartBeatTime = System.currentTimeMillis();
    }


    public void hearBeat(){
        if(!heatHeart){
            executorService.scheduleAtFixedRate(new HeartBeatTask(this),0,PEARID, TimeUnit.MILLISECONDS);
            heatHeart = true;
        }
    }

    private boolean heatTimeout() {
        return (System.currentTimeMillis() - lastHeartBeatTime) > HEATBEAT_TIME_OUT;
    }


    /**
     * 这里return还需要改一下。。。
     * @param packet
     * @return
     */
    public ChannelFuture send(Packet packet) {

        if(channel.isActive()) {

            ChannelFuture future = channel.writeAndFlush(packet);

            if (channel.isWritable()) {
                return future;
            }
            //阻塞调用线程还是抛异常？
            //return channel.newPromise().setFailure(new RuntimeException("send data too busy"));
            if (!future.channel().eventLoop().inEventLoop()) {
                future.awaitUninterruptibly(100);
            }
            return future;
        }
        return null;

    }


    class HeartBeatTask implements Runnable{
        private Connection connection;

        public HeartBeatTask(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            if(heatTimeout()){

                new DouyuHeartbeatMessage(null,connection)
                        .send()
                        .addListener((future -> {
                            if(future.isSuccess()) {
                                refresHeartBeat();
                                Log.defLogger.info("ping a heat beat...");
                            }
                        }));

            }
        }
    }
}
