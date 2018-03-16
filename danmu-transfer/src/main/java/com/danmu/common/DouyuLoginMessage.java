package com.danmu.common;

import com.danmu.protocol.Packet;

import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:登录相关的消息
 * @date 2018/3/16
 */
public class DouyuLoginMessage extends DouyuMessage {

    //type@=loginres/userid@=0/roomgroup@=0/pg@=0/sessionid@=0/us
    //ername@=/nickname@=/live_stat@=0/is_illegal@=0/ill_ct@=0/ill_ts@
    //=0/best_now@=0/ps@=0/es@=0/it@=0/its@=0/npv@=0/best_dlev@
    //=0/cur_lev@=0/nrc@=0/ih@=0/sid@=0/sahf@=0/

    public DouyuLoginMessage(Packet packet, SocketChannel channel) {
        super(packet, channel);
    }

    public Message decode() {
        return null;
    }


}
