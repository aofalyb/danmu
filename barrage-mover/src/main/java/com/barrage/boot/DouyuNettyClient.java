package com.barrage.boot;


import com.barrage.common.Log;
import com.barrage.message.DouyuLoginMessage;
import com.barrage.netty.Connection;
import com.barrage.netty.DouyuConnClientChannelHandler;
import com.barrage.netty.NettyClientException;
import io.netty.channel.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class DouyuNettyClient extends NettyClient {

    private String rid;

    private static CountDownLatch loginFuture = new CountDownLatch(1);

    private Thread reConnectThread;

    public DouyuNettyClient(String rid) {
        this.rid = rid;
    }

    //接收login success的回调(重连也会调用)
    public static void notifyLoginSuccess() {
        loginFuture.countDown();
    }


    @Override
    public ChannelHandler getChannelHandler() {
        return new DouyuConnClientChannelHandler(rid);
    }

    @Override
    public CountDownLatch login() throws NettyClientException {


        try {
            init();
            ChannelFuture connect = connect("openbarrage.douyutv.com", 8601);
            connect.get(DouyuLoginMessage.LOGIN_TIME_OUT, TimeUnit.MILLISECONDS);
            if(!connect.isSuccess()) {
                throw new NettyClientException("connect fail ,rid="+rid);
            }

            //超时检查
            if(!loginFuture.await(DouyuLoginMessage.LOGIN_TIME_OUT, TimeUnit.MILLISECONDS)) {
                throw new NettyClientException("login time out ,rid="+rid);
            }

        } catch (Throwable throwable) {
            if(!(throwable instanceof NettyClientException)) {
                throw new NettyClientException("login error ,rid="+rid,throwable);
            }
            throw (NettyClientException) throwable;
        }
        return loginFuture;
    }

    @Override
    public void reLogin(Connection connection) {
        if(reConnectThread != null) return;

        reConnectThread = new Thread(() -> {
            int times = 0;
            while (connection.getState() != Connection.ConnectionState.JOINED) {
                Log.errorLogger.error("reconnect rid={} , times = {}.",rid,++times);
                try {
                    ChannelFuture connect = connect("openbarrage.douyutv.com", 8601);
                    connect.get(DouyuLoginMessage.LOGIN_TIME_OUT, TimeUnit.MILLISECONDS);
                    if(!connect.isSuccess()) {
                        throw new NettyClientException("connect fail ,rid="+rid);
                    }

                    //超时检查
                    if(!loginFuture.await(DouyuLoginMessage.LOGIN_TIME_OUT, TimeUnit.MILLISECONDS)) {
                        throw new NettyClientException("login time out ,rid="+rid);
                    }

                } catch (Exception e) {
                   Log.errorLogger.error("reconnect-thread",e);
                } finally {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }

            }
            Log.errorLogger.error("reconnect rid={} success, retry times = {}.",rid,times);

        });
        reConnectThread.setName("reconnect-thread");
        reConnectThread.start();

    }
}
