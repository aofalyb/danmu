package com.barrage.transport;

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

    private long lastReadTime = -1;

    private long lastWriteTime = -1;

    private volatile long lastHeartBeatTime = -1;

    private static final long HEATBEAT_TIME_OUT = 40 * 1000;

    private static final long PEARID = 1 * 1000;

    private volatile boolean heatHeart = false;

    private String rid;

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public Connection(Channel channel,String rid) {
        this.channel = channel;
        this.rid = rid;
    }


    public void refreshRead(){
        lastReadTime = System.currentTimeMillis();
    }

    public void refreshWrite(){
        lastWriteTime = System.currentTimeMillis();
    }

    private void refresHeartBeat(){
        lastHeartBeatTime = System.currentTimeMillis();
    }

    public boolean isValid(){

        return true;
    }

    public boolean shutDown(){

        return true;
    }



    public void hearBeat(){
        if(!heatHeart){
            executorService.scheduleAtFixedRate(new HeartBeatTask(),0,PEARID, TimeUnit.MILLISECONDS);
            heatHeart = true;
        }
    }


    class HeartBeatTask implements Runnable{

        @Override
        public void run() {

            if((System.currentTimeMillis() - lastHeartBeatTime) > HEATBEAT_TIME_OUT){
                Log.defLogger.info("ping a heat beat...");
                channel.writeAndFlush(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_HEARTBEAT,rid)).addListener((future -> {
                    if(future.isSuccess()){
                        lastHeartBeatTime = System.currentTimeMillis();
                    }
                }));

            }
        }
    }
}
