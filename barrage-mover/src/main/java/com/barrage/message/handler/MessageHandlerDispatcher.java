package com.barrage.message.handler;


import com.barrage.message.Message;
import com.barrage.transport.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息处理器分发
 */
public class MessageHandlerDispatcher {

    private Map<String,IMessageHandler> handlerMap;

    private Connection connection;


    public MessageHandlerDispatcher(Connection connection) {
        handlerMap = new HashMap<>();
        this.connection = connection;
    }

    public void regiter(String key, IMessageHandler messageHandler) {
        handlerMap.put(key,messageHandler);
    }


    public IMessageHandler getHandler(String key) {

        return handlerMap.get(key);
    }


    public boolean handle(Message message) {

        IMessageHandler handler = getHandler(message.getMessageType());
        if(handler == null) {
            throw new MessageHandleRuntimeException("can`t find any handler match key "+message.getMessageType()+".");
        }

        return handler.handle(connection,message);
    }







}
