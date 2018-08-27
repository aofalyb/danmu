package com.barrage.boot;


import com.barrage.message.DouyuLoginMessage;
import com.barrage.netty.DouyuConnClientChannelHandler;
import com.barrage.netty.Listener;
import com.barrage.netty.NettyClientException;
import io.netty.channel.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class DouyuNettyClient extends NettyClient {

    private String rid;
    private Listener listener;

    private volatile boolean loginSuccess = false;


    public DouyuNettyClient(String rid) {
        this.rid = rid;
    }

    interface LoginListener extends Listener {

    }

    @Override
    public ChannelHandler getChannelHandler() {
        return new DouyuConnClientChannelHandler(rid, new LoginListener() {
            @Override
            public void onSuccess(Object... args) {
                if(listener != null) {
                    listener.onSuccess(args);
                    loginSuccess = true;
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                if(listener != null) {
                    listener.onFailure(cause);
                }
            }
        });
    }

    @Override
    public void login(Listener listener) {

        this.listener = listener;

        try {
            init(listener);
            ChannelFuture connect = connect("openbarrage.douyutv.com", 8601);
            connect.get(DouyuLoginMessage.LOGIN_TIME_OUT, TimeUnit.MILLISECONDS);
            if(!connect.isSuccess()) {
                listener.onFailure(null);
            }

            //超时检查
            Thread.sleep(DouyuLoginMessage.LOGIN_TIME_OUT);
            if(!loginSuccess) {
                listener.onFailure(new NettyClientException("login time out ,rid="+rid));
            }

        } catch (Throwable throwable) {
            listener.onFailure(throwable);
            return;
        }
    }



}
