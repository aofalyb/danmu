package com.danmu.common;

import com.danmu.protocol.DouyuPacket;
import com.danmu.protocol.Packet;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class DouyuPacketBuilder {


    public static Packet build(byte cmd,String content){

        if(cmd == DouyuPacket.PACKET_TYPE_LOGIN){

            String loginWrap = "type@=loginreq/roomid@="+content+"/";
            Packet packet = new DouyuPacket(loginWrap);

            return packet;
        }

        if(cmd == DouyuPacket.PACKET_TYPE_JOINGROUP){

            String loginWrap = "type@=joingroup/rid@="+content+"/gid@=-9999/";
            Packet packet = new DouyuPacket(loginWrap);

            return packet;
        }

        return null;
    }
}
