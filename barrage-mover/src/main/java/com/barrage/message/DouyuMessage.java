package com.barrage.message;

import com.barrage.transport.Connection;
import com.barrage.common.Log;
import com.barrage.protocol.DouyuPacket;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyang
 * @description:斗鱼弹幕协议（小端整数），基于《斗鱼弹幕服务器第三方接入协议v1.6.2》
 * @date 2018/3/16
 */
public class DouyuMessage extends BaseMessage <DouyuPacket> {

    private Map<String,String> attributes = new HashMap();

    public DouyuMessage(DouyuPacket packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void decode() {

        byte[] douyuPacketBody = packet.getBody();

        if(douyuPacketBody != null){
            //斗鱼把消息协议放到消息体内...
            String originMsg = null;
            try {
                originMsg = new String(douyuPacketBody,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.errorLogger.error(e);
            }
            attributes = DouyuSerializeUtil.unSerialize(new String(douyuPacketBody));
            Log.defLogger.info("#["+System.currentTimeMillis()+"]"+originMsg);
        }
    }



    public Map<String, String> getAttributes() {
        return attributes;
    }
}
