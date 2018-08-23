package com.barrage.util;

import com.barrage.protocol.DouyuPacket;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class DouyuPacketBuilder {


    public static DouyuPacket build(byte cmd,String content){

        if(cmd == DouyuPacket.PACKET_TYPE_LOGIN){

            String loginWrap = "type@=loginreq/roomid@="+content+"/";
            DouyuPacket packet = new DouyuPacket(loginWrap.getBytes());

            return packet;
        }

        if(cmd == DouyuPacket.PACKET_TYPE_JOINGROUP){

            String loginWrap = "type@=joingroup/rid@="+content+"/gid@=-9999/";
            DouyuPacket packet = new DouyuPacket(loginWrap.getBytes());

            return packet;
        }

        if(cmd == DouyuPacket.PACKET_TYPE_HEARTBEAT){

            String loginWrap = "type@=mrkl/";
            DouyuPacket packet = new DouyuPacket(loginWrap.getBytes());

            return packet;
        }

        return null;
    }
}
