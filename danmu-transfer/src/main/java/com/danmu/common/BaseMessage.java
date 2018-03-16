package com.danmu.common;

import com.danmu.protocol.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public abstract class BaseMessage implements Message{

    protected Packet packet;

    private SocketChannel channel;

    public BaseMessage(Packet packet, SocketChannel channel) {
        this.packet = packet;
        this.channel = channel;
    }


    public void send(OnMessageSendListener onMessageSendListener) {

        ByteBuffer buffer = encode();

        if(channel.isConnected()){

            int remaining = buffer.remaining();

            try {
                int writedLength = channel.write(buffer);

                if(remaining == writedLength){
                    onMessageSendListener.onSuccess();
                    return;
                }

                onMessageSendListener.onError();

            } catch (Exception e) {
                onMessageSendListener.onError();
                Log.e(null,e);
                try {
                    channel.close();
                } catch (IOException e1) {
                    Log.e("client close...",e1);
                }
            }
        }

    }
}
