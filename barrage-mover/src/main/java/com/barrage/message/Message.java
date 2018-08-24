package com.barrage.message;


import io.netty.channel.ChannelFuture;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface Message {



    ChannelFuture send();

    void decode();

    void encode();

}
