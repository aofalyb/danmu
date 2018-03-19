package com.danmu.common;

import java.nio.ByteBuffer;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface Message {



    void send(OnMessageSendListener onMessageSendListener);

}
