package com.barrage.netty;

import com.barrage.message.DouyuHeartbeatMessage;
import com.barrage.message.DouyuLoginMessage;
import com.barrage.protocol.Packet;
import com.barrage.common.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author liyang
 * @description: C/S连接
 * @date 2018/3/19
 */
public class Connection {


    /**
     * new 新建
     * LOGIN_PRE 已发送登录消息但未收到响应
     * LOGINED 已收到登录响应消息，登录成功
     * JOINED 已发送入组消息（入组消息没有响应，发送成功即为入组成功，只有此时才是正常状态）
     * INACTIVE client断线
     */
    public enum ConnectionState {

        NEW,LOGIN_PRE,LOGINED,JOINED,INACTIVE;

        private Date changeTime;

        ConnectionState() {
        }

        ConnectionState(Date changeTime) {
            this.changeTime = changeTime;
        }

        public Date getChangeTime() {
            return changeTime;
        }

        public void setChangeTime(Date changeTime) {
            this.changeTime = changeTime;
        }

    }

    public static final long HEATBEAT_TIME_OUT = 40 * 1000;

    private Channel channel;
    private volatile long lastReadTime = -1;
    private volatile long lastHeartBeatTime = -1;

    private volatile boolean heatHeart = false;

    private volatile ConnectionState state = ConnectionState.NEW;

    private String rid;
    //维持心跳的线程，所有的实例共享。
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public Connection(Channel channel,String rid) {
        this.channel = channel;
        this.rid = rid;
    }

    public String getRid() {
        return rid;
    }


    public void refreshRead(){
        lastReadTime = System.currentTimeMillis();
    }

    /**
     * ping -> pong
     */
    private void refreshHeartBeat(){
        lastHeartBeatTime = System.currentTimeMillis();
    }

    public void refreshState(ConnectionState newState) {
        this.state = newState;
    }



    public void hearBeat(){
        if(!heatHeart){
            executorService.scheduleAtFixedRate(new HeartBeatTask(this),0,HEATBEAT_TIME_OUT, TimeUnit.MILLISECONDS);
            heatHeart = true;
        }
    }

    public boolean heatTimeout() {
        return (System.currentTimeMillis() - lastHeartBeatTime) > HEATBEAT_TIME_OUT;
    }


    /**
     * reconnect util login success again
     */
    public void reConnect(Consumer<Connection> login) {

        Thread reConnectThread = new Thread(() -> {
            int times = 0;
            while (state != ConnectionState.JOINED) {
                Log.errorLogger.error("reconnect rid={} , times = {}.",rid,++times);
                try {
                    login.accept(this);
                } catch (Exception e) {
                   Log.errorLogger.error("reconnect-thread",e);
                }
            }
        });
        reConnectThread.setName("reconnect-thread");
        reConnectThread.start();
    }


    /**
     * 这里return还需要改一下。。。
     * @param packet
     * @return
     */
    public ChannelFuture send(Packet packet) {

//        if(state != ConnectionState.JOINED) {
//            throw new NettyClientRuntimeException("conn state changed,current state: "+state+", need state: "+ConnectionState.JOINED);
//        }

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
        } else {
            throw new NettyClientRuntimeException("channel is inactive.");
        }
    }



    class HeartBeatTask implements Runnable{
        private Connection connection;

        public HeartBeatTask(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {

            new DouyuHeartbeatMessage(null,connection)
                    .send()
                    .addListener((future -> {
                        if(future.isSuccess()) {
                            refreshHeartBeat();
                        }
                    }));

        }
    }



}
