package com.barrage.message.handler;

import com.barrage.message.Message;
import com.barrage.transport.Connection;

public interface IMessageHandler {

    boolean handle(Connection connection,Message message);
}
