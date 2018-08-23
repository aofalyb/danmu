package com.barrage.common;

import com.barrage.api.Connection;
import com.barrage.protocol.DouyuPacket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyang
 * @description:斗鱼弹幕协议（小端整数），基于《斗鱼弹幕服务器第三方接入协议v1.6.2》
 * @date 2018/3/16
 */
public class DouyuMessage extends BaseMessage {



    private Map<String,String> attributes = new HashMap();

    public DouyuMessage(DouyuPacket packet, Connection connection) {
        super(packet, connection);
    }


    @Override
    public void decode() {

        byte[] douyuPacketBody = ((DouyuPacket) packet).getBody();

        if(douyuPacketBody != null){
            //斗鱼把消息协议放到消息体内...
            attributes = DouyuSerializeUtil.unSerialize(new String(douyuPacketBody));
        }
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}
