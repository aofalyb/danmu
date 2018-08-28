package com.barrage.message.handler;

import com.barrage.common.Log;
import com.barrage.elastic.EsClient;
import com.barrage.message.DouyuMessage;
import com.barrage.message.Message;
import com.barrage.netty.Connection;

import java.util.Map;

public class DouyuDefaultMessageHandler implements IMessageHandler<DouyuMessage> {

    private static EsClient esClient;

    static {
        esClient = EsClient.getInstance();
    }

    @Override
    public boolean handle(Connection connection, DouyuMessage message) {
        String messageType = message.getMessageType();

        Map<String, String> attributes = message.getAttributes();
        String nn = attributes.get("nn");
        String txt = attributes.get("txt");
        String uid = attributes.get("uid");
        try {
            esClient.insert(uid,nn,txt);
        } catch (Exception e) {
            Log.errorLogger.error("insert into es error, nn={},txt={},uid={}",nn,txt,uid,e);
        }


        return false;
    }
}
