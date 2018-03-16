package com.danmu.protocol;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class Packet {

    //序列化部分
    private String content;

    public Packet() {
    }

    public Packet(String content) {
        this.content = content;
    }
}
