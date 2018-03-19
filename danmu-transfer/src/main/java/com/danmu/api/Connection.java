package com.danmu.api;

import com.danmu.common.DouyuMessage;
import com.danmu.common.DouyuPacketBuilder;
import com.danmu.common.Log;
import com.danmu.common.OnMessageSendListener;
import com.danmu.protocol.DouyuPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liyang
 * @description: C/S连接
 * @date 2018/3/19
 */
public class Connection {

    private SocketChannel channel;

    private long lastReadTime = -1;

    private long lastWriteTime = -1;

    private long lastHeartBeatTime = -1;

    private static final long HEATBEAT_TIME_OUT = 40 * 1000;

    private static final long PEARID = 1 * 1000;

    private String rid;

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public Connection(SocketChannel channel,String rid) {
        this.channel = channel;
        this.rid = rid;
        executorService.scheduleAtFixedRate(new HeartBeatTask(),0,PEARID, TimeUnit.MILLISECONDS);
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


    public boolean send(ByteBuffer buffer,OnMessageSendListener onMessageSendListener) {

        int remaining = buffer.remaining();

        try {
            int writedLength = channel.write(buffer);

            if(remaining == writedLength){
                onMessageSendListener.onSuccess();
                return true;
            }

            onMessageSendListener.onError();

            return false;

        } catch (Exception e) {
            onMessageSendListener.onError();
            Log.e(null,e);
            try {
                channel.close();
            } catch (IOException e1) {
                Log.e("client close...",e1);
            }

            return false;
        }

    }


    class HeartBeatTask implements Runnable{

        public void run() {

            if((System.currentTimeMillis() - lastHeartBeatTime) > HEATBEAT_TIME_OUT){
                Log.d("ping a heat beat...");
                send(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_HEARTBEAT,rid).encode(), new OnMessageSendListener() {
                    public void onSuccess() {
                        refresHeartBeat();
                    }

                    public void onError() {

                    }
                });
            }
        }
    }
}
