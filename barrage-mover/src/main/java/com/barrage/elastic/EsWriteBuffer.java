package com.barrage.elastic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class EsWriteBuffer {

    public static final int DEFAULT_BUFFER_LENGTH = 1000;
    private BlockingQueue<EsWriteObject> buffered  = null;


    private int bufferLen = DEFAULT_BUFFER_LENGTH;

    public EsWriteBuffer(int bufferLen) {
        this.bufferLen = bufferLen;
        buffered = new LinkedBlockingQueue<>(bufferLen);
    }

    /**
     * cache if buffer is full
     * @param t
     * @return true if cached
     */
    public synchronized boolean cache(EsWriteObject t) {
        if(buffered.size() < DEFAULT_BUFFER_LENGTH) {
            return buffered.add(t);
        } else {
            return false;
        }
    }

    public BlockingQueue<EsWriteObject> get() {
        return buffered;
    }

    public void reset() {
        buffered.clear();
    }
}
