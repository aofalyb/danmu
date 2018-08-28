package com.barrage.message.handler;


import com.barrage.message.Message;
import com.barrage.netty.Connection;
import com.barrage.netty.Listener;

public interface IMessageHandler<T extends Message> {

    boolean handle(Connection connection, T message);
}
