package com.danmu.common;


/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface Message {



    void send(OnMessageSendListener onMessageSendListener);

    void decode();

}
