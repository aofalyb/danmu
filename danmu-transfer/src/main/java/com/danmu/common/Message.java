package com.danmu.common;

import java.nio.ByteBuffer;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface Message {


    Message decode();


    ByteBuffer encode();


    void send(OnMessageSendListener onMessageSendListener);

}
