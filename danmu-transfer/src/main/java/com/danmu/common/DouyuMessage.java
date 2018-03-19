package com.danmu.common;

import com.danmu.api.Connection;
import com.danmu.protocol.DouyuPacket;
import com.danmu.protocol.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyang
 * @description:斗鱼弹幕协议（小端整数），基于《斗鱼弹幕服务器第三方接入协议v1.6.2》
 * @date 2018/3/16
 */
public class DouyuMessage extends BaseMessage {


    private DouyuPacket douyuPacket = (DouyuPacket) packet;

    protected Map<String,String> attributes = new HashMap();

    public DouyuMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public Map getAttributes() {
        return douyuPacket.decode();
    }

}
