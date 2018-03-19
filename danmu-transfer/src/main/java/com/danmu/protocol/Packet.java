package com.danmu.protocol;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public abstract class Packet {

    //序列化部分
    private String content;

    public Packet() {
    }

    public Packet(String content) {
        this.content = content;
    }

    abstract public ByteBuffer encode();

    abstract public Map decode();
}
